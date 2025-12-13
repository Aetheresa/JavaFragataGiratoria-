package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Producto;
import com.proyecto.fragataGiratoria.service.ProductoService;

// === Importaciones para PDF (iText 5) ===
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font; 
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// === Importaciones para Excel (Apache POI) ===
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.util.IOUtils;
// =============================================

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

    // Método auxiliar para agregar logo al PDF (extraído para evitar try anidado)
    private void addLogoToPDF(Document document) {
        try {
            ClassPathResource logoResource = new ClassPathResource("static/img/icono-fragata.jpg");
            InputStream logoStream = logoResource.getInputStream();
            Image logo = Image.getInstance(IOUtils.toByteArray(logoStream));
            logo.scaleToFit(100, 100); // Escalar a 100x100 píxeles
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
            document.add(new Paragraph("\n"));
        } catch (Exception e) {
            // Si no se encuentra la imagen, continuar sin ella
            System.out.println("Logo no encontrado: " + e.getMessage());
        }
    }

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
            
            // Agregar imagen del logo usando método auxiliar
            addLogoToPDF(document);
            
            // Configuración de la Fuente
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(255, 215, 0)); // Dorado
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            Font fontData = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            Font fontDataAlt = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK); // Para filas alternas
            
            // Título
            Paragraph title = new Paragraph("Reporte de Productos - La Fragata Giratoria", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); 

            // Tabla 
            PdfPTable table = new PdfPTable(7); // 7 columnas
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 2f, 2f, 1f, 1f, 2f});

            // Headers de la tabla con color negro
            String[] headers = {"ID", "Nombre", "Vencimiento", "Precio", "Stock Act.", "Stock Min.", "Unidad"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK); // Negro
                cell.setPadding(8); 
                cell.setBorderColor(BaseColor.WHITE);
                cell.setBorderWidth(1f);
                table.addCell(cell);
            }

            // Datos de la tabla - MANEJO DE NULOS MEJORADO con filas alternas
            boolean alternate = false;
            for (Producto producto : listaProductos) {
                BaseColor rowColor = alternate ? new BaseColor(255, 215, 0) : BaseColor.WHITE; // Dorado alterno
                alternate = !alternate;
                
                // ID
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(producto.getIdProducto()), fontData));
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                idCell.setBackgroundColor(rowColor);
                idCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(idCell);

                // Nombre
                String nombre = producto.getNombre() != null ? producto.getNombre() : "N/D";
                PdfPCell nombreCell = new PdfPCell(new Phrase(nombre, fontData));
                nombreCell.setPadding(5);
                nombreCell.setBackgroundColor(rowColor);
                nombreCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(nombreCell);
                
                // Fecha Vencimiento
                String fecha = (producto.getFechaRegistro() != null) ? producto.getFechaRegistro().toString() : "N/A";
                PdfPCell fechaCell = new PdfPCell(new Phrase(fecha, fontData));
                fechaCell.setPadding(5);
                fechaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                fechaCell.setBackgroundColor(rowColor);
                fechaCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(fechaCell);
                
                // Precio Unitario 
                PdfPCell precioCell = new PdfPCell(new Phrase(String.valueOf(producto.getPrecioUnitario()), fontData));
                precioCell.setPadding(5);
                precioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                precioCell.setBackgroundColor(rowColor);
                precioCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(precioCell);
                
                // Stock Actual
                PdfPCell stockActCell = new PdfPCell(new Phrase(String.valueOf(producto.getStockActual()), fontData));
                stockActCell.setPadding(5);
                stockActCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                stockActCell.setBackgroundColor(rowColor);
                stockActCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(stockActCell);
                
                // Stock Mínimo
                PdfPCell stockMinCell = new PdfPCell(new Phrase(String.valueOf(producto.getStockMinimo()), fontData));
                stockMinCell.setPadding(5);
                stockMinCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                stockMinCell.setBackgroundColor(rowColor);
                stockMinCell.setBorderColor(new BaseColor(200, 200, 200));
                table.addCell(stockMinCell);
                
                // Unidad Medida
                String unidad = producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "N/D";
                PdfPCell unidadCell = new PdfPCell(new Phrase(unidad, fontData));
                unidadCell.setPadding(5);
                unidadCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                unidadCell.setBackgroundColor(rowColor);
                unidadCell.setBorderColor(new BaseColor(200, 200, 200));
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

    // Método auxiliar para agregar logo al Excel (extraído para evitar try anidado)
    private void addLogoToExcel(Sheet sheet, Workbook workbook) {
        try {
            ClassPathResource logoResource = new ClassPathResource("static/img/icono-fragata.jpg");
            InputStream logoStream = logoResource.getInputStream();
            byte[] bytes = IOUtils.toByteArray(logoStream);
            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG); // Cambiado a JPEG
            XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 3, 5); // Posición: columnas 0-3, filas 0-5
            XSSFPicture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize(3, 5); // Ajustar tamaño
        } catch (Exception e) {
            // Si no se encuentra la imagen, continuar sin ella
            System.out.println("Logo no encontrado: " + e.getMessage());
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
            
            // Agregar imagen del logo usando método auxiliar
            addLogoToExcel(sheet, workbook);
            
            // Estilos para headers (negro con dorado)
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex()); // Blanco para contraste
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // Negro
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Estilo para datos alternos (dorado)
            CellStyle dataStyle = workbook.createCellStyle();
            CellStyle dataAltStyle = workbook.createCellStyle();
            XSSFColor goldColor = new XSSFColor(new byte[]{(byte) 255, (byte) 215, (byte) 0}); // Dorado personalizado con byte array
            dataAltStyle.setFillForegroundColor(goldColor);
            dataAltStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            Row headerRow = sheet.createRow(6); // Empezar después del logo
            
            String[] headers = {"ID", "Nombre", "Fecha Vencimiento", "Precio Unitario", "Stock Actual", "Stock Mínimo", "Unidad Medida"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i); 
            }
            
            int rowCount = 7; // Continuar después del header
            boolean alternate = false;
            for (Producto producto : listaProductos) {
                Row dataRow = sheet.createRow(rowCount++);
                CellStyle currentStyle = alternate ? dataAltStyle : dataStyle;
                alternate = !alternate;
                int columnCount = 0;

                Cell idCell = dataRow.createCell(columnCount++);
                idCell.setCellValue(producto.getIdProducto());
                idCell.setCellStyle(currentStyle);
                
                Cell nombreCell = dataRow.createCell(columnCount++);
                nombreCell.setCellValue(producto.getNombre());
                nombreCell.setCellStyle(currentStyle);
                
                // Manejo de nulos en Fecha
                String fecha = (producto.getFechaRegistro() != null) ? producto.getFechaRegistro().toString() : "N/A";
                Cell fechaCell = dataRow.createCell(columnCount++);
                fechaCell.setCellValue(fecha);
                fechaCell.setCellStyle(currentStyle);
                
                Cell precioCell = dataRow.createCell(columnCount++);
                precioCell.setCellValue(producto.getPrecioUnitario());
                precioCell.setCellStyle(currentStyle);
                
                Cell stockActCell = dataRow.createCell(columnCount++);
                stockActCell.setCellValue(producto.getStockActual());
                stockActCell.setCellStyle(currentStyle);
                
                Cell stockMinCell = dataRow.createCell(columnCount++);
                stockMinCell.setCellValue(producto.getStockMinimo());
                stockMinCell.setCellStyle(currentStyle);
                
                Cell unidadCell = dataRow.createCell(columnCount++);
                unidadCell.setCellValue(producto.getUnidadMedida());
                unidadCell.setCellStyle(currentStyle);
            }

            workbook.write(outputStream);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Manejo de errores
        }
    }
}
