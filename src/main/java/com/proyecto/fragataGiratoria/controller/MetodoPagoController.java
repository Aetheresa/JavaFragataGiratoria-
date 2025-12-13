package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.MetodoPago;
import com.proyecto.fragataGiratoria.service.MetodoPagoService;

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

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/crud/metodosdepago") // ✅ RUTA FINAL CORREGIDA (PLURAL)
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    // Inyección de dependencias (se asume que existe MetodoPagoService)
    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    // ------------------------------------------------------------------
    // --- 1. LEER (READ) - Listar Métodos de Pago ---
    // ------------------------------------------------------------------
    @GetMapping
    public String mostrarMetodosPago(Model model) {
        List<MetodoPago> metodos = metodoPagoService.listarMetodosPago();
        model.addAttribute("metodosPago", metodos);
        model.addAttribute("titulo", "Gestión de Métodos de Pago");
        // Nombre del archivo: metodosdepago.html
        return "roles/admin/crud/metododepago/metodosdepago"; 
    }

    // ------------------------------------------------------------------
    // --- 2. CREAR & ACTUALIZAR (CREATE & UPDATE) ---
    // ------------------------------------------------------------------

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("metodoPago", new MetodoPago());
        model.addAttribute("titulo", "Crear Nuevo Método de Pago");
        // Nombre del archivo: pagoscrear.html
        return "roles/admin/crud/metododepago/pagoscrear"; 
    }

    @PostMapping
    public String guardarMetodoPago(@ModelAttribute MetodoPago metodoPago, RedirectAttributes attributes) {
        try {
            metodoPagoService.guardar(metodoPago);
            String accion = (metodoPago.getIdMetodoPago() == null) ? "creado" : "actualizado";
            attributes.addFlashAttribute("success", "Método de pago " + accion + " exitosamente.");
        } catch (Exception e) {
             attributes.addFlashAttribute("error", "Error al guardar el método de pago: " + e.getMessage());
        }
        // Redirección a la lista con la RUTA PLURAL
        return "redirect:/crud/metodosdepago"; 
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Integer id, Model model) {
        MetodoPago metodoPago = metodoPagoService.obtenerMetodoPagoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado con ID: " + id));

        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("titulo", "Editar Método de Pago: " + metodoPago.getNombreMetodo());
        // Nombre del archivo: pagosedit.html
        return "roles/admin/crud/metododepago/pagosedit"; 
    }

    // ------------------------------------------------------------------
    // --- 3. ELIMINAR (DELETE) ---
    // ------------------------------------------------------------------
    @GetMapping("/eliminar/{id}")
    public String eliminarMetodoPago(@PathVariable Integer id, RedirectAttributes attributes) {
        try {
            metodoPagoService.eliminar(id);
            attributes.addFlashAttribute("success", "Método de pago eliminado exitosamente.");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error: No se pudo eliminar el método de pago (puede estar asociado a registros).");
        }
        // Redirección a la lista con la RUTA PLURAL
        return "redirect:/crud/metodosdepago"; 
    }

    // ------------------------------------------------------------------
    // --- 4. EXPORTACIÓN A PDF (iText 5) ---
    // ------------------------------------------------------------------
    @GetMapping("/export/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=metodos_pago.pdf");

        List<MetodoPago> metodos = metodoPagoService.listarMetodosPago();

        try (OutputStream outputStream = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Estilos de fuente
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            // Título
            Paragraph title = new Paragraph("Reporte de Métodos de Pago", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Tabla (Solo 2 columnas: ID y Nombre)
            PdfPTable table = new PdfPTable(2); 
            table.setWidthPercentage(80);
            table.setWidths(new float[]{1f, 4f});
            table.setSpacingBefore(10);

            // Cabecera de la tabla
            String[] headers = {"ID", "Nombre del Método"}; 
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Datos de los métodos de pago
            for (MetodoPago metodo : metodos) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(metodo.getIdMetodoPago()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(metodo.getNombreMetodo(), dataFont))); 
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
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=metodos_pago.xlsx");

        List<MetodoPago> metodos = metodoPagoService.listarMetodosPago();

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("MetodosPago");

            // Estilo de Cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Fila de Cabecera (Solo 2 columnas: ID y Nombre)
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre del Método"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos de las filas
            int rowNum = 1;
            for (MetodoPago metodo : metodos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(metodo.getIdMetodoPago());
                row.createCell(1).setCellValue(metodo.getNombreMetodo());
            }

            // Ajustar el ancho de las columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}