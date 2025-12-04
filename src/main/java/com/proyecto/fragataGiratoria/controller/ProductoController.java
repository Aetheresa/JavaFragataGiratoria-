package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.repository.ProductoRepository;

import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // ------------------------
    // LISTAR + FILTRO
    // ------------------------
    @GetMapping
    public String index(@RequestParam(value = "search", required = false) String search, 
                        Model model) {

        List<Producto> productos;

        if (search != null && !search.trim().isEmpty()) {
            productos = productoRepository.buscar(search.trim());
        } else {
            productos = productoRepository.findAll();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("search", search);

        return "roles/admin/crud/crud_productos/index";
    }

    // ------------------------
    // CREAR
    // ------------------------
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "roles/admin/crud/crud_productos/crear";
    }

    // ------------------------
    // GUARDAR
    // ------------------------
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        // Verificar si el stock_actual es null y asignar un valor predeterminado
        if (producto.getStockActual() == null) {
            producto.setStockActual(0);  // Asignamos 0 si es null
        }

        // Validar precio
        if (producto.getPrecio() == null) {
            producto.setPrecio(0.0);  // Asignamos 0.0 si es null
        }

        // Validar nombre
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            return "redirect:/productos/crear";  // Redirige al formulario de creación si el nombre está vacío
        }

        // Guardar el producto en la base de datos
        productoRepository.save(producto);
        return "redirect:/productos";  // Redirige a la lista de productos
    }

    // ------------------------
    // EDITAR FORMULARIO
    // ------------------------
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) return "redirect:/productos";

        model.addAttribute("producto", producto);
        return "roles/admin/crud/crud_productos/editar";
    }

    // ------------------------
    // ACTUALIZAR
    // ------------------------
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Producto productoActualizado) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) return "redirect:/productos";

        producto.setNombre(productoActualizado.getNombre());
        producto.setDescripcion(productoActualizado.getDescripcion());
        producto.setCategoria(productoActualizado.getCategoria());
        producto.setCodigo(productoActualizado.getCodigo());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setActivo(productoActualizado.getActivo());
        producto.setStock(productoActualizado.getStock());
        producto.setStockMinimo(productoActualizado.getStockMinimo());

        // Verificar y actualizar el stock_actual
        if (productoActualizado.getStockActual() == null) {
            producto.setStockActual(0);  // Asignar 0 si es null
        } else {
            producto.setStockActual(productoActualizado.getStockActual());
        }

        productoRepository.save(producto);
        return "redirect:/productos";
    }

    // ------------------------
    // ELIMINAR
    // ------------------------
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
        }
        return "redirect:/productos";
    }

    // ------------------------
    // EXPORTAR PDF
    // ------------------------
    @GetMapping("/export/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=productos.pdf");

        List<Producto> productos = productoRepository.findAll();

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("Reporte de Productos", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 3f, 2f, 2f, 2f, 1.5f, 2f, 2f, 2f});

        String[] headers = {"ID", "Nombre", "Descripción", "Categoría", "Código", "Precio",
                "Activo", "Stock", "Stock Mínimo", "Stock Actual"};

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (Producto p : productos) {
            table.addCell(str(p.getId()));
            table.addCell(str(p.getNombre()));
            table.addCell(str(p.getDescripcion()));
            table.addCell(str(p.getCategoria()));
            table.addCell(str(p.getCodigo()));
            table.addCell(str(p.getPrecio()));
            table.addCell(str(p.getActivo()));
            table.addCell(str(p.getStock()));
            table.addCell(str(p.getStockMinimo()));
            table.addCell(str(p.getStockActual()));
        }

        document.add(table);
        document.close();
    }

    // ------------------------
    // EXPORTAR EXCEL
    // ------------------------
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=productos.xlsx");

        List<Producto> productos = productoRepository.findAll();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Productos");

        int fila = 0;

        // Encabezados
        Row header = sheet.createRow(fila++);
        String[] cols = {"ID", "Nombre", "Descripción", "Categoría", "Código", "Precio",
                "Activo", "Stock", "Stock Mínimo", "Stock Actual"};

        for (int i = 0; i < cols.length; i++) {
            header.createCell(i).setCellValue(cols[i]);
        }

        // Datos
        for (Producto p : productos) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(num(p.getId()));
            row.createCell(1).setCellValue(str(p.getNombre()));
            row.createCell(2).setCellValue(str(p.getDescripcion()));
            row.createCell(3).setCellValue(str(p.getCategoria()));
            row.createCell(4).setCellValue(str(p.getCodigo()));
            row.createCell(5).setCellValue(num(p.getPrecio()));
            row.createCell(6).setCellValue(p.getActivo() != null && p.getActivo());
            row.createCell(7).setCellValue(num(p.getStock()));
            row.createCell(8).setCellValue(num(p.getStockMinimo()));
            row.createCell(9).setCellValue(num(p.getStockActual()));
        }

        for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // ------------------------
    // MÉTODOS AUXILIARES
    // ------------------------
    private String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private double num(Number n) {
        return n == null ? 0 : n.doubleValue();
    }
}
