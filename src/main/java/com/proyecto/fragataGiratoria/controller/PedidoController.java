package com.proyecto.fragataGiratoria.controller;

import com.proyecto.fragataGiratoria.model.Pedido;
import com.proyecto.fragataGiratoria.service.PedidoService;

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
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("/crud/pedidos")
public class PedidoController {

    private final PedidoService pedidoService; 
    // Formato de fecha para mostrar en exports
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Inyección de dependencias
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ------------------------------------------------------------------
    // --- 1. LEER (READ) - Listar Pedidos ---
    // ------------------------------------------------------------------
    // Mapea a: GET /crud/pedidos
    @GetMapping
    public String mostrarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        model.addAttribute("pedidos", pedidos);
        // Retorna la vista: /templates/roles/admin/crud/pedidos/pedidos.html
        return "roles/admin/crud/pedidos/pedidos"; 
    }

    // ------------------------------------------------------------------
    // --- 2. CREAR & ACTUALIZAR (CREATE & UPDATE) ---
    // ------------------------------------------------------------------

    // Mostrar formulario de NUEVO Pedido
    // Mapea a: GET /crud/pedidos/nuevo
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        // Retorna la vista: /templates/roles/admin/crud/pedidos/pedidoscrear.html
        return "roles/admin/crud/pedidos/pedidoscrear"; 
    }
    
    // Mapeo POST para CREACIÓN y ACTUALIZACIÓN
    // Mapea a: POST /crud/pedidos
    @PostMapping
    public String guardarPedido(@ModelAttribute Pedido pedido) {
        pedidoService.guardarPedido(pedido);
        return "redirect:/crud/pedidos";
    }
    
    // Mostrar formulario de EDICIÓN
    // Mapea a: GET /crud/pedidos/editar/{id}
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) { 
        Pedido pedido = pedidoService.obtenerPedidoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con ID: " + id));

        model.addAttribute("pedido", pedido); 
        // Retorna la vista: /templates/roles/admin/crud/pedidos/pedidosedit.html
        return "roles/admin/crud/pedidos/pedidosedit"; 
    }

    // ------------------------------------------------------------------
    // --- 3. ELIMINAR (DELETE) ---
    // ------------------------------------------------------------------
    // Mapea a: GET /crud/pedidos/eliminar/{id}
    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return "redirect:/crud/pedidos"; 
    }

    // ------------------------------------------------------------------
    // --- 4. EXPORTACIÓN A PDF (iText 5) ---
    // ------------------------------------------------------------------
    // Mapea a: GET /crud/pedidos/export/pdf
    @GetMapping("/export/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos.pdf");

        List<Pedido> pedidos = pedidoService.listarPedidos();

        try (OutputStream outputStream = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Estilos de fuente
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

            // Título
            Paragraph title = new Paragraph("Reporte de Pedidos - La Fragata Giratoria", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Tabla
            PdfPTable table = new PdfPTable(4); // Solo mostramos 4 columnas clave
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 2f, 3f});
            table.setSpacingBefore(10);
            
            // Cabecera de la tabla
            String[] headers = {"ID", "Fecha", "Total", "Estado"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Datos de los pedidos
            for (Pedido pedido : pedidos) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(pedido.getId()), dataFont)));
                table.addCell(new PdfPCell(new Phrase(pedido.getFecha().format(DATE_FORMATTER), dataFont)));
                // Formatear el total como moneda (simple)
                table.addCell(new PdfPCell(new Phrase("$" + pedido.getTotal().toString(), dataFont))); 
                table.addCell(new PdfPCell(new Phrase(pedido.getEstado(), dataFont)));
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Podrías manejar el error con un mensaje al usuario
        }
    }


    // ------------------------------------------------------------------
    // --- 5. EXPORTACIÓN A EXCEL (Apache POI) ---
    // ------------------------------------------------------------------
    // Mapea a: GET /crud/pedidos/export/excel
    @GetMapping("/export/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos.xlsx");

        List<Pedido> pedidos = pedidoService.listarPedidos();

        try (Workbook workbook = new XSSFWorkbook(); 
             OutputStream outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Pedidos");
            
            // Estilo de Cabecera
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Fila de Cabecera (usando todos los campos importantes)
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Cantidad", "Estado", "Fecha", "Observaciones", "Precio Unitario", "Subtotal", "Total", "ID Cliente"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos de las filas
            int rowNum = 1;
            for (Pedido pedido : pedidos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(pedido.getId());
                row.createCell(1).setCellValue(pedido.getCantidad());
                row.createCell(2).setCellValue(pedido.getEstado());
                row.createCell(3).setCellValue(pedido.getFecha().format(DATE_FORMATTER));
                row.createCell(4).setCellValue(pedido.getObservaciones());
                row.createCell(5).setCellValue(pedido.getPrecioUnitario().doubleValue());
                row.createCell(6).setCellValue(pedido.getSubtotal().doubleValue());
                row.createCell(7).setCellValue(pedido.getTotal().doubleValue());
                row.createCell(8).setCellValue(pedido.getIdCliente());
            }

            // Ajustar el ancho de las columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            // Manejar errores de exportación
        }
    }
}