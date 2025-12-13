package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Compra;
import com.proyecto.fragataGiratoria.service.CompraService;

// === Importaciones para PDF (iText 5) ===
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font; 
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// =======================================

// === Importaciones para Excel (Apache POI) ===
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// =============================================

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream; 
import java.util.List;
import java.util.Optional; 
import java.time.format.DateTimeFormatter; // Para formatear la fecha

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/crud/compras")
public class CompraController {

    private final CompraService compraService; 
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    // --- 1. LEER (READ) ---
    @GetMapping
    public String mostrarCompras(Model model) {
        List<Compra> compras = compraService.listarCompras();
        model.addAttribute("compras", compras);
        return "roles/admin/crud/compras/compras"; 
    }

    // ------------------------------------------------------------------
    // --- 2. CREAR & ACTUALIZAR (CREATE & UPDATE) ---
    // ------------------------------------------------------------------

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("compra", new Compra());
        return "roles/admin/crud/compras/comprascrear"; 
    }
    
    @PostMapping
    public String guardarCompra(@ModelAttribute Compra compra) {
        compraService.guardarCompra(compra);
        return "redirect:/crud/compras";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) { 
        Compra compra = compraService.obtenerCompraPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada con ID: " + id));

        model.addAttribute("compra", compra); 
        return "roles/admin/crud/compras/comprasedit"; 
    }

    // --- 3. ELIMINAR (DELETE) ---
    @GetMapping("/eliminar/{id}")
    public String eliminarCompra(@PathVariable Long id) {
        compraService.eliminarCompra(id);
        return "redirect:/crud/compras"; 
    }

    // ------------------------------------------------------------------
    // --- 4. EXPORTACIÓN A PDF (iText 5) ---
    // ------------------------------------------------------------------

    @GetMapping("/export/pdf")
    public void exportarComprasPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=compras_" + System.currentTimeMillis() + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Compra> listaCompras = compraService.listarCompras();
        
        try (OutputStream outputStream = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Estilos... (código omitido por brevedad, asumiendo que los estilos son correctos)
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            Font fontData = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

            Paragraph title = new Paragraph("Reporte de Compras - La Fragata Giratoria", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); 

            // Tabla (4 columnas: ID, Descripción, Fecha, Total)
            PdfPTable table = new PdfPTable(4); 
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 4f, 2f, 2f}); 

            // Headers
            String[] headers = {"ID", "Descripción", "Fecha", "Total"}; 
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setPadding(6); 
                table.addCell(cell);
            }

            // Datos
            for (Compra compra : listaCompras) {
                
                // 1. ID (Corrección: getId() en lugar de getIdCompra())
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(compra.getId()), fontData));
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                // 2. Descripción
                String descripcion = compra.getDescripcion() != null ? compra.getDescripcion() : "N/D";
                PdfPCell descCell = new PdfPCell(new Phrase(descripcion, fontData));
                descCell.setPadding(5);
                table.addCell(descCell);
                
                // 3. Fecha (Corrección: getFecha() en lugar de getFechaCompra())
                String fecha = compra.getFecha() != null ? compra.getFecha().format(DATE_FORMATTER) : "N/D";
                PdfPCell fechaCell = new PdfPCell(new Phrase(fecha, fontData));
                fechaCell.setPadding(5);
                table.addCell(fechaCell);
                
                // 4. Total
                String total = String.valueOf(compra.getTotal()); 
                PdfPCell totalCell = new PdfPCell(new Phrase(total, fontData));
                totalCell.setPadding(5);
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(totalCell);
            }

            document.add(table);
            document.close();
            
        } catch (Exception e) { 
            e.printStackTrace();
        } 
    }

    // ------------------------------------------------------------------
    // --- 5. EXPORTACIÓN A EXCEL (Apache POI) ---
    // ------------------------------------------------------------------

    @GetMapping("/export/excel")
    public void exportarComprasExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=compras_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Compra> listaCompras = compraService.listarCompras();

        try (Workbook workbook = new XSSFWorkbook(); 
             OutputStream outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Compras");
            Row row = sheet.createRow(0);
            
            // Estilos... (código omitido)

            String[] headers = {"ID", "Descripción", "Fecha", "Total"}; 
            
            // Llenar encabezados... (código omitido)
            CellStyle style = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
            excelFont.setBold(true); 
            style.setFont(excelFont); 
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
            }
            
            int rowCount = 1;
            for (Compra compra : listaCompras) {
                Row dataRow = sheet.createRow(rowCount++);
                int columnCount = 0;

                // 1. ID (Corrección: getId() en lugar de getIdCompra())
                dataRow.createCell(columnCount++).setCellValue(compra.getId());
                
                // 2. Descripción
                String descripcion = compra.getDescripcion() != null ? compra.getDescripcion() : "N/D"; 
                dataRow.createCell(columnCount++).setCellValue(descripcion);
                
                // 3. Fecha (Corrección: getFecha() en lugar de getFechaCompra())
                String fecha = compra.getFecha() != null ? compra.getFecha().format(DATE_FORMATTER) : "N/D";
                dataRow.createCell(columnCount++).setCellValue(fecha);
                
                // 4. Total (Corrección: Convertir BigDecimal a double)
                dataRow.createCell(columnCount++).setCellValue(compra.getTotal().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}