package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.model.Proveedor;
import com.proyecto.fragataGiratoria.repository.ProductoRepository;
import com.proyecto.fragataGiratoria.repository.ProveedorRepository;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/productos")  // ← IMPORTANTE: Se queda en /productos
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    // ----------------------------------------------------
    // LISTAR
    // ----------------------------------------------------
    @GetMapping
    public String index(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "CRUD_Productos/index";
    }

    // ----------------------------------------------------
    // CREAR - FORMULARIO
    // ----------------------------------------------------
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "CRUD_Productos/crear";
    }

    // ----------------------------------------------------
    // GUARDAR
    // ----------------------------------------------------
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {
        productoRepository.save(producto);
        return "redirect:/productos";
    }

    // ----------------------------------------------------
    // EDITAR
    // ----------------------------------------------------
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        model.addAttribute("producto", producto);
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "CRUD_Productos/editar";
    }

    // ----------------------------------------------------
    // ACTUALIZAR
    // ----------------------------------------------------
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Producto productoActualizado) {

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto inválido"));

        producto.setNombreProducto(productoActualizado.getNombreProducto());
        producto.setUnidadMedida(productoActualizado.getUnidadMedida());
        producto.setStockActual(productoActualizado.getStockActual());
        producto.setStockMinimo(productoActualizado.getStockMinimo());
        producto.setProveedor(productoActualizado.getProveedor());

        productoRepository.save(producto);

        return "redirect:/productos";
    }

    // ----------------------------------------------------
    // ELIMINAR
    // ----------------------------------------------------
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoRepository.deleteById(id);
        return "redirect:/productos";
    }

    // ----------------------------------------------------
    // EXPORTAR PDF
    // ----------------------------------------------------
    @GetMapping("/export/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException, DocumentException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=productos.pdf");

        List<Producto> productos = productoRepository.findAll();

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // ---- CORRECCIÓN DEL FONT DE iTEXT ----
        com.itextpdf.text.Font tituloFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA,
                18,
                com.itextpdf.text.Font.BOLD
        );

        Paragraph titulo = new Paragraph("Reporte de Productos", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 2, 2, 3});

        String[] headers = {"Nombre", "Unidad", "Stock Actual", "Stock Mínimo", "Proveedor"};

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (Producto p : productos) {
            table.addCell(valorSeguro(p.getNombreProducto()));
            table.addCell(valorSeguro(p.getUnidadMedida()));
            table.addCell(String.valueOf(p.getStockActual()));
            table.addCell(String.valueOf(p.getStockMinimo()));
            table.addCell(valorSeguro(p.getProveedor()));
        }

        document.add(table);
        document.close();
    }

    // ----------------------------------------------------
    // EXPORTAR EXCEL
    // ----------------------------------------------------
    @GetMapping("/export/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=productos.xlsx");

        List<Producto> productos = productoRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Productos");

        String[] headers = {"Nombre", "Unidad", "Stock Actual", "Stock Mínimo", "Proveedor"};

        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell c = headerRow.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        int idx = 1;
        for (Producto p : productos) {
            Row row = sheet.createRow(idx++);

            row.createCell(0).setCellValue(valorSeguro(p.getNombreProducto()));
            row.createCell(1).setCellValue(valorSeguro(p.getUnidadMedida()));
            row.createCell(2).setCellValue(p.getStockActual());
            row.createCell(3).setCellValue(p.getStockMinimo());
            row.createCell(4).setCellValue(valorSeguro(p.getProveedor()));
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // ----------------------------------------------------
    // EXPORTAR IMAGEN
    // ----------------------------------------------------
    @GetMapping("/export/{format}")
    public void exportarImagen(@PathVariable String format, HttpServletResponse response) throws IOException {

        List<Producto> productos = productoRepository.findAll();

        int width = 1000;
        int height = 60 + productos.size() * 30;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 16));

        g.drawString("REPORTE DE PRODUCTOS", 380, 30);

        int y = 60;
        g.drawString("Nombre", 20, y);
        g.drawString("Unidad", 250, y);
        g.drawString("Stock Actual", 450, y);
        g.drawString("Stock Mínimo", 650, y);
        g.drawString("Proveedor", 850, y);

        y += 25;

        for (Producto p : productos) {
            g.drawString(valorSeguro(p.getNombreProducto()), 20, y);
            g.drawString(valorSeguro(p.getUnidadMedida()), 250, y);
            g.drawString(String.valueOf(p.getStockActual()), 450, y);
            g.drawString(String.valueOf(p.getStockMinimo()), 650, y);
            g.drawString(valorSeguro(p.getProveedor()), 850, y);
            y += 25;
        }

        g.dispose();

        if (!format.equalsIgnoreCase("png") && !format.equalsIgnoreCase("jpg")) {
            format = "png";
        }

        response.setContentType("image/" + format);
        response.setHeader("Content-Disposition", "attachment; filename=productos." + format);

        ImageIO.write(image, format, response.getOutputStream());
    }

    // ----------------------------------------------------
    // MÉTODOS AUXILIARES
    // ----------------------------------------------------
    private String valorSeguro(Proveedor proveedor) {
        return (proveedor != null) ? proveedor.getNombreProveedor() : "";
    }

    private String valorSeguro(String s) {
        return (s != null) ? s : "";
    }
}