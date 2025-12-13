package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Platillo;
import com.proyecto.fragataGiratoria.service.PlatilloService;

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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/crud/platillos")
public class PlatilloController {

    private final PlatilloService platilloService; 

    // Inyección de dependencias limpia (Sin @Autowired)
    public PlatilloController(PlatilloService platilloService) {
        this.platilloService = platilloService;
    }

    // --- 1. LEER (READ) ---
    @GetMapping
    public String mostrarPlatillos(Model model) {
        List<Platillo> platillos = platilloService.listarPlatillos();
        model.addAttribute("platillos", platillos);
        return "roles/admin/crud/platillos/platillos";
    }

    // ------------------------------------------------------------------
    // --- 2. CREAR & ACTUALIZAR (CREATE & UPDATE) ---
    // ------------------------------------------------------------------

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("platillo", new Platillo());
        return "roles/admin/crud/platillos/nuevo";
    }
    
    // Mapeo POST para la CREACIÓN
    @PostMapping // Mapea a POST /crud/platillos
    public String guardarPlatillo(@ModelAttribute Platillo platillo) {
    platilloService.guardarPlatillo(platillo);
    return "redirect:/crud/platillos";
}
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Platillo platillo = platilloService.obtenerPlatilloPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Platillo no encontrado con ID: " + id));

        model.addAttribute("platillo", platillo); 
        return "roles/admin/crud/platillos/platilloedit"; 
    }

    // --- 4. ELIMINAR (DELETE) ---

    @GetMapping("/eliminar/{id}")
    public String eliminarPlatillo(@PathVariable Long id) {
        platilloService.eliminarPlatillo(id);
        return "redirect:/crud/platillos"; 
    }

    // ------------------------------------------------------------------
    // --- 5. EXPORTACIÓN A PDF (iText 5) ---
    // ------------------------------------------------------------------

    @GetMapping("/export/pdf")
    public void exportarPlatillosPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=platillos_" + System.currentTimeMillis() + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Platillo> listaPlatillos = platilloService.listarPlatillos();
        
        try (OutputStream outputStream = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            Font fontData = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);

            Paragraph title = new Paragraph("Reporte de Platillos - La Fragata Giratoria", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n")); 

            PdfPTable table = new PdfPTable(5); 
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 4f, 2f, 2f}); 

            // Headers
            String[] headers = {"ID", "Nombre", "Descripción", "Precio", "Estado"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLUE.darker());
                cell.setPadding(6); 
                cell.setBorderColor(BaseColor.WHITE);
                table.addCell(cell);
            }

            // Datos
            for (Platillo platillo : listaPlatillos) {
                // ID
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(platillo.getIdPlatillo()), fontData));
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                // Nombre
                String nombre = platillo.getNombre() != null ? platillo.getNombre() : "N/D";
                PdfPCell nombreCell = new PdfPCell(new Phrase(nombre, fontData));
                nombreCell.setPadding(5);
                table.addCell(nombreCell);
                
                // Descripción 
                String descripcion = platillo.getDescripcion() != null ? platillo.getDescripcion() : "N/D";
                PdfPCell descCell = new PdfPCell(new Phrase(descripcion, fontData));
                descCell.setPadding(5);
                table.addCell(descCell);
                
                // Precio 
                PdfPCell precioCell = new PdfPCell(new Phrase(String.valueOf(platillo.getPrecio()), fontData));
                precioCell.setPadding(5);
                precioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(precioCell);
                
                // ⭐ ESTADO: Usa "N/D" (Valor Fijo) para resolver el error de compilación de getEstado()
                String estado = "N/D"; 
                PdfPCell estadoCell = new PdfPCell(new Phrase(estado, fontData));
                estadoCell.setPadding(5);
                estadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(estadoCell);
            }

            document.add(table);
            document.close();
            
        } catch (Exception e) { 
            e.printStackTrace();
        } 
    }

    // ------------------------------------------------------------------
    // --- 6. EXPORTACIÓN A EXCEL (Apache POI) ---
    // ------------------------------------------------------------------

    @GetMapping("/export/excel")
    public void exportarPlatillosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=platillos_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Platillo> listaPlatillos = platilloService.listarPlatillos();

        try (Workbook workbook = new XSSFWorkbook(); 
             OutputStream outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Platillos");
            Row row = sheet.createRow(0);
            
            CellStyle style = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font excelFont = workbook.createFont();
            
            excelFont.setBold(true); 
            style.setFont(excelFont); 

            String[] headers = {"ID", "Nombre", "Descripción", "Precio", "Estado"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
            }
            
            int rowCount = 1;
            for (Platillo platillo : listaPlatillos) {
                Row dataRow = sheet.createRow(rowCount++);
                int columnCount = 0;

                dataRow.createCell(columnCount++).setCellValue(platillo.getIdPlatillo());
                dataRow.createCell(columnCount++).setCellValue(platillo.getNombre());
                
                // Descripción 
                String descripcion = platillo.getDescripcion() != null ? platillo.getDescripcion() : "N/D"; 
                dataRow.createCell(columnCount++).setCellValue(descripcion);
                
                dataRow.createCell(columnCount++).setCellValue(platillo.getPrecio());
                
                // ⭐ ESTADO: Usa "N/D" (Valor Fijo) para resolver el error de compilación de getEstado()
                String estado = "N/D"; 
                dataRow.createCell(columnCount++).setCellValue(estado);
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