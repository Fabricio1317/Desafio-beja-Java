package com.becajava.ms_transaction_api.core.usecase;

import com.becajava.ms_transaction_api.core.domain.Transacao;
import com.becajava.ms_transaction_api.core.exception.RegraDeNegocioException; // <--- Import Novo
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class GerarExtratoPdfUseCase {

    public byte[] gerar(List<Transacao> transacoes) {

        if (transacoes == null || transacoes.isEmpty()) {
            throw new RegraDeNegocioException("A lista de transações está vazia. Impossível gerar PDF.");
        }

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("Relatório Financeiro - Beca Java", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            table.setWidths(new float[]{1.5f, 3f, 2f, 1.5f, 2f});

            adicionarCabecalho(table);
            adicionarDados(table, transacoes);

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro interno ao gerar o arquivo PDF", e);
        }

        return out.toByteArray();
    }

    private void adicionarCabecalho(PdfPTable table) {
        String[] colunas = {"Data", "Descrição", "Categoria", "Tipo", "Valor"};
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

        for (String coluna : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(coluna, fontHeader));
            cell.setBackgroundColor(new BaseColor(0, 51, 102));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
    }

    private void adicionarDados(PdfPTable table, List<Transacao> transacoes) {
        Font fontDados = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DateTimeFormatter dataFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Transacao t : transacoes) {
            String dataStr = (t.getDataCriacao() != null) ? t.getDataCriacao().format(dataFmt) : "-";
            PdfPCell cellData = new PdfPCell(new Phrase(dataStr, fontDados));
            cellData.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellData);

            table.addCell(new Phrase(t.getDescricao() != null ? t.getDescricao() : "Sem descrição", fontDados));
            table.addCell(new Phrase(t.getCategoria() != null ? t.getCategoria() : "Geral", fontDados));
            table.addCell(new Phrase(t.getTipo() != null ? t.getTipo() : "-", fontDados));

            String valorFormatado = (t.getValor() != null) ? moeda.format(t.getValor()) : "R$ 0,00";
            PdfPCell cellValor = new PdfPCell(new Phrase(valorFormatado, fontDados));
            cellValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cellValor);
        }
    }
}