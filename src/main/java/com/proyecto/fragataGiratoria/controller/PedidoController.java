package com.proyecto.fragataGiratoria.controller;

// ... (Importaciones omitidas por brevedad)

import com.proyecto.fragataGiratoria.model.Pedido;
import com.proyecto.fragataGiratoria.repository.ClienteRepository;
import com.proyecto.fragataGiratoria.repository.UsuarioRepository; 
import com.proyecto.fragataGiratoria.service.PedidoService;
import com.proyecto.fragataGiratoria.service.PlatilloService; 
import com.proyecto.fragataGiratoria.service.UsuarioService; 

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream; 
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional; 
import java.math.BigDecimal; 
import java.text.DecimalFormat; 

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

// === Importaciones para PDF (iText 5) ===
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
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
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@Controller
@RequestMapping("/crud/pedidos")
public class PedidoController {

    // ==================================================================
    // 1. DEPENDENCIAS E INYECCIÓN
    // ==================================================================

    private final PedidoService pedidoService;
    private final PlatilloService platilloService;
    private final UsuarioService usuarioService; 
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository; 

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DecimalFormat MONEY_FORMATTER = new DecimalFormat("#,##0.00");

    public PedidoController(
        PedidoService pedidoService, 
        PlatilloService platilloService, 
        UsuarioService usuarioService, 
        ClienteRepository clienteRepository,
        UsuarioRepository usuarioRepository) 
    {
        this.pedidoService = pedidoService;
        this.platilloService = platilloService;
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ==================================================================
    // 2. OPERACIONES CRUD
    // ==================================================================

    @GetMapping
    public String mostrarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        model.addAttribute("pedidos", pedidos);
        return "roles/admin/crud/pedidos/pedidos"; 
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("platillos", platilloService.listarPlatillos()); 
        model.addAttribute("clientes", clienteRepository.findAll()); 
        model.addAttribute("usuarios", usuarioService.listarTodos()); 
        return "roles/admin/crud/pedidos/pedidoscrear"; 
    }
    
    @PostMapping
    public String guardarPedido(@ModelAttribute Pedido pedido) {
        
        // 1. Recargar Cliente (Relación JPA) - Se utiliza el getter/setter corregido de Pedido.java
        if (pedido.getCliente() != null && pedido.getCliente().getIdCliente() != null) {
            clienteRepository.findById(pedido.getCliente().getIdCliente())
                             .ifPresent(pedido::setCliente);
        }
        
        // 2. Recargar Usuario (Relación JPA) - Se utiliza el getter/setter corregido de Pedido.java
        if (pedido.getUsuario() != null && pedido.getUsuario().getIdUsuario() != null) {
            usuarioRepository.findById(pedido.getUsuario().getIdUsuario())
                             .ifPresent(pedido::setUsuario);
        }
        
        // 3. Asignar Nombre del Platillo usando idPlatillo
        if (pedido.getIdPlatillo() != null) {
            platilloService.obtenerPlatilloPorId(pedido.getIdPlatillo().longValue()) // Asumiendo que el servicio usa Long
                .ifPresent(platillo -> {
                    pedido.setNombrePlatillo(platillo.getNombre()); // <-- Setter corregido
                });
        }

        // 4. Inicializar Estados
        if (pedido.getId() == null) { 
            if (pedido.getEstadoCocina() == null || pedido.getEstadoCocina().isEmpty()) {
                pedido.setEstadoCocina("PENDIENTE"); 
            }
            if (pedido.getEstadoMesero() == null || pedido.getEstadoMesero().isEmpty()) {
                pedido.setEstadoMesero("EN ESPERA");
            }
            if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
                pedido.setEstado("ABIERTO");
            }
        }

        // 5. Guardar el pedido
        pedidoService.guardarPedido(pedido);
        return "redirect:/crud/pedidos";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) { 
        Pedido pedido = pedidoService.obtenerPedidoPorId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con ID: " + id));

        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("platillos", platilloService.listarPlatillos());
        model.addAttribute("usuarios", usuarioService.listarTodos());

        return "roles/admin/crud/pedidos/pedidosedit"; 
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return "redirect:/crud/pedidos"; 
    }

    // ==================================================================
    // 3. FUNCIONES DE EXPORTACIÓN (PDF) - Se mantiene igual
    // ==================================================================
    
    @GetMapping("/export/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException {
        // ... (Código PDF omitido por brevedad, no tenía errores de compilación)
    }

    private void addLogoToPDF(Document document) {
        // ... (Código PDF omitido)
    }

    // ==================================================================
    // 4. FUNCIONES DE EXPORTACIÓN (EXCEL)
    // ==================================================================

    @GetMapping("/export/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos_" + System.currentTimeMillis() + ".xlsx");

        List<Pedido> pedidos = pedidoService.listarPedidos();

        try (Workbook workbook = new XSSFWorkbook(); 
             OutputStream outputStream = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Pedidos");
            
            addLogoToExcel(sheet, workbook);
            
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = workbook.createCellStyle();
            CellStyle dataAltStyle = createDataAltStyle(workbook);

            String[] headers = {"ID", "Cantidad", "Estado", "Fecha", "Observaciones", "Precio Unitario", "Subtotal", "Total", "ID Usuario", "Platillo", "Cliente"};
            createHeaderRow(sheet, headerStyle, headers, 6);

            fillDataRows(sheet, pedidos, dataStyle, dataAltStyle, headers.length, 7);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // --- Métodos de apoyo refactorizados para Excel ---

    private CellStyle createHeaderStyle(Workbook workbook) {
        // ... (Método omitido)
        return null;
    }
    
    private CellStyle createDataAltStyle(Workbook workbook) {
        // ... (Método omitido)
        return null;
    }

    private void createHeaderRow(Sheet sheet, CellStyle style, String[] headers, int rowNum) {
        // ... (Método omitido)
    }

    private void fillDataRows(Sheet sheet, List<Pedido> pedidos, CellStyle dataStyle, CellStyle dataAltStyle, int numColumns, int startRow) {
        int rowNum = startRow; 
        boolean alternate = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Pedido pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);
            CellStyle currentStyle = alternate ? dataAltStyle : dataStyle;
            alternate = !alternate;

            // Datos del Pedido
            row.createCell(0).setCellValue(pedido.getId() != null ? pedido.getId() : 0);
            row.createCell(1).setCellValue(pedido.getCantidad() != null ? pedido.getCantidad() : 0);
            row.createCell(2).setCellValue(pedido.getEstado());
            row.createCell(3).setCellValue(pedido.getFecha() != null ? pedido.getFecha().format(formatter) : "N/A");
            row.createCell(4).setCellValue(pedido.getObservaciones());
            
            // CORRECCIÓN 1: Precio Unitario (Es Double, se usa directamente)
            row.createCell(5).setCellValue(pedido.getPrecioUnitario() != null ? pedido.getPrecioUnitario() : 0.0);
            
            // CORRECCIÓN 2 y 3 (Errores 8, 9): Usar .doubleValue() para convertir BigDecimal a double
            row.createCell(6).setCellValue(pedido.getSubtotal() != null ? pedido.getSubtotal().doubleValue() : 0.0);
            row.createCell(7).setCellValue(pedido.getTotal() != null ? pedido.getTotal().doubleValue() : 0.0);

            // Relaciones
            // Se usa getUsuario() y getIdUsuario() (Getters corregidos)
            String idUsuario = (pedido.getUsuario() != null && pedido.getUsuario().getIdUsuario() != null) 
                                ? pedido.getUsuario().getIdUsuario().toString() : "N/A";
                                
            row.createCell(8).setCellValue(idUsuario); 
            
            // Se usa getNombrePlatillo() (Getter corregido)
            row.createCell(9).setCellValue(pedido.getNombrePlatillo() != null ? pedido.getNombrePlatillo() : "N/A");
            
            // Se usa getCliente() y getNombre() (Getters corregidos)
            row.createCell(10).setCellValue(pedido.getCliente() != null ? pedido.getCliente().getNombre() : "N/A");
            
            // Asignar estilo
            for (int i = 0; i < numColumns; i++) {
                if(row.getCell(i) != null) {     
                    row.getCell(i).setCellStyle(currentStyle);
                }
            }
        }
    }
    
    private void addLogoToExcel(Sheet sheet, Workbook workbook) {
        // ... (Método omitido)
    }
}