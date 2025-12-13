package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.service.ProductoService;

// === Importaciones para PDF (iText 5) ===
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font; 
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// === Importaciones para Excel (Apache POI) ===
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// =============================================

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // --- MÉTODOS CRUD ---

    @GetMapping
    public String mostrarProductos(Model model) {
        List<Producto> productos = productoService.findAll();
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("productosBajoStock", productoService.countBajoStock());
        model.addAttribute("totalProveedores", productoService.countProveedores()); 
        return "roles/admin/crud/crud_productos/index";
    }

    @GetMapping("/crear")
    public String crearProducto(Model model) {
        model.addAttribute("producto", new Producto());
        return "roles/admin/crud/crud_productos/crear";
    }

    @PostMapping("/crear")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoService.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.findById(id)
             .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        
        model.addAttribute("producto", producto);
        
        return "roles/admin/crud/crud_productos/editar";
    }

    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable Long id, @ModelAttribute Producto producto) {
        producto.setIdProducto(id);
        productoService.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.delete(id);
        return "redirect:/productos";
    }

    // ------------------------------------------------------------------
    // --- MÉTODOS DE EXPORTACIÓN (CORRECCIÓN FINAL) ---
    // ------------------------------------------------------------------

    @GetMapping("/export/pdf")
    public void exportarProductosPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=productos_" + System.currentTimeMillis() + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Producto> listaProductos = productoService.findAll();
        
        Document document = new Document();
        OutputStream outputStream = null; // Declaramos el stream fuera del try-with-resources
        
        try {
            outputStream = response.getOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Configuración de la Fuente
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            Font fontData = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            
            // Título
            Paragraph title = new Paragraph("Reporte de Productos - La Fragata Giratoria", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); 

            // Tabla 
            PdfPTable table = new PdfPTable(7); // 7 columnas
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 2f, 2f, 1f, 1f, 2f});

            // Headers de la tabla
            String[] headers = {"ID", "Nombre", "Vencimiento", "Precio", "Stock Act.", "Stock Min.", "Unidad"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setPadding(6); 
                cell.setBorderColor(BaseColor.WHITE);
                table.addCell(cell);
            }

            // Datos de la tabla - MANEJO DE NULOS MEJORADO
            for (Producto producto : listaProductos) {
                
                // ID
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(producto.getIdProducto()), fontData));
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                // Nombre
                String nombre = producto.getNombre() != null ? producto.getNombre() : "N/D";
                PdfPCell nombreCell = new PdfPCell(new Phrase(nombre, fontData));
                nombreCell.setPadding(5);
                table.addCell(nombreCell);
                
                // Fecha Vencimiento
                String fecha = producto.getFechaVencimiento() != null ? producto.getFechaVencimiento().toString() : "N/A";
                PdfPCell fechaCell = new PdfPCell(new Phrase(fecha, fontData));
                fechaCell.setPadding(5);
                fechaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(fechaCell);
                
                // Precio Unitario 
                PdfPCell precioCell = new PdfPCell(new Phrase(String.valueOf(producto.getPrecioUnitario()), fontData));
                precioCell.setPadding(5);
                precioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(precioCell);
                
                // Stock Actual
                PdfPCell stockActCell = new PdfPCell(new Phrase(String.valueOf(producto.getStockActual()), fontData));
                stockActCell.setPadding(5);
                stockActCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(stockActCell);
                
                // Stock Mínimo
                PdfPCell stockMinCell = new PdfPCell(new Phrase(String.valueOf(producto.getStockMinimo()), fontData));
                stockMinCell.setPadding(5);
                stockMinCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(stockMinCell);
                
                // Unidad Medida
                String unidad = producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "N/D";
                PdfPCell unidadCell = new PdfPCell(new Phrase(unidad, fontData));
                unidadCell.setPadding(5);
                unidadCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(unidadCell);
            }

            document.add(table);
            
        } catch (Exception e) { 
             e.printStackTrace();
        } finally {
            if (document.isOpen()) {
                document.close(); // Finaliza el PDF
            }
            // ************ CORRECCIÓN CLAVE ************
            if (outputStream != null) {
                 outputStream.flush(); // Forzar la entrega de los bytes al cliente
            }
            // ******************************************
        }
    }

    @GetMapping("/export/excel")
    public void exportarProductosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=productos_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Producto> listaProductos = productoService.findAll();

        try (Workbook workbook = new XSSFWorkbook(); 
             OutputStream outputStream = response.getOutputStream()) {

            // Lógica de Excel integrada (Apache POI) 
            Sheet sheet = workbook.createSheet("Productos");
            Row row = sheet.createRow(0);
            
            // Creamos CellStyle de POI
            CellStyle style = workbook.createCellStyle();
            
            // Creamos Font de POI (Corregido: org.apache.poi.ss.usermodel.Font)
            org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
            
            // Configuramos la fuente (métodos de POI)
            excelFont.setBold(true); 
            style.setFont(excelFont); // Asignamos la fuente de POI

            String[] headers = {"ID", "Nombre", "Fecha Vencimiento", "Precio Unitario", "Stock Actual", "Stock Mínimo", "Unidad Medida"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
                sheet.autoSizeColumn(i); 
            }
            
            int rowCount = 1;
            for (Producto producto : listaProductos) {
                Row dataRow = sheet.createRow(rowCount++);
                int columnCount = 0;

                dataRow.createCell(columnCount++).setCellValue(producto.getIdProducto());
                dataRow.createCell(columnCount++).setCellValue(producto.getNombre());
                
                // Manejo de nulos en Fecha
                String fecha = (producto.getFechaVencimiento() != null) ? producto.getFechaVencimiento().toString() : "N/A";
                dataRow.createCell(columnCount++).setCellValue(fecha);
                
                dataRow.createCell(columnCount++).setCellValue(producto.getPrecioUnitario());
                dataRow.createCell(columnCount++).setCellValue(producto.getStockActual());
                dataRow.createCell(columnCount++).setCellValue(producto.getStockMinimo());
                dataRow.createCell(columnCount++).setCellValue(producto.getUnidadMedida());
            }

            workbook.write(outputStream);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Manejo de errores
        }
    }
}