package com.example.eHall.Utils;

import com.example.eHall.Entity.Acte.ActeNaissance;
import com.example.eHall.Entity.Utilisateur.Parent;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Générateur d'Acte de Naissance au format PDF.
 * Utilise iText 7 pour produire un document fidèle au formulaire officiel camerounais.
 */
public class ActeGenerator {

    private final ActeNaissance acteNaissance;
    private PdfFont fontNormal;
    private PdfFont fontBold;
    private PdfFont fontItalic;

    private static final float FONT_SIZE_NORMAL = 9f;
    private static final float FONT_SIZE_SMALL  = 7.5f;
    private static final float FONT_SIZE_TITLE  = 13f;
    private static final float FONT_SIZE_HEADER = 8f;

    // Chemin racine du projet (répertoire courant d'exécution)
    private static final String OUTPUT_DIR = "";

    public ActeGenerator(ActeNaissance acteNaissance) {
        this.acteNaissance = acteNaissance;
    }

    /**
     * Génère le PDF de l'acte de naissance.
     *
     * @return le chemin absolu du fichier généré
     * @throws IOException en cas d'erreur d'écriture
     */
    public String generer() throws IOException {
        String fileName = "acte_naissance_" + acteNaissance.getNumeroActe() + ".pdf";
        String filePath = Paths.get(OUTPUT_DIR, fileName).toAbsolutePath().toString();

        fontNormal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        fontBold   = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        fontItalic = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filePath));
             Document document = new Document(pdfDoc, PageSize.A4)) {

            document.setMargins(25, 40, 25, 40);

            // ── En-tête institutionnel ──────────────────────────────────────────
            ajouterEntete(document);

            // ── Titre ───────────────────────────────────────────────────────────
            ajouterTitre(document);

            // ── Corps : informations de l'enfant et des parents ─────────────────
            ajouterCorps(document);

            // ── Section déclaration / certification ─────────────────────────────
            ajouterSectionDeclaration(document);

            // ── Zone de signatures ───────────────────────────────────────────────
            ajouterSignatures(document);
        }

        return filePath;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EN-TÊTE
    // ═══════════════════════════════════════════════════════════════════════════

    private void ajouterEntete(Document document) throws IOException {
        String nomMairie = acteNaissance.getDeclaration() != null
                && acteNaissance.getDeclaration().getMairie() != null
                ? acteNaissance.getDeclaration().getMairie().getNom()
                : "________________";

        // Tableau à 3 colonnes : gauche (localisation) | centre (état civil) | droite (devise)
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{35, 30, 35}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER);

        // Colonne gauche – subdivision administrative
        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        leftCell.add(labelSmall("PROVINCE"));
        leftCell.add(valeurRouge("DE L'OUEST"));
        leftCell.add(new Paragraph(" ").setFontSize(2));
        leftCell.add(labelSmall("DEPARTEMENT\nDIVISION"));
        leftCell.add(valeurRouge("KOUNG-KHI"));
        leftCell.add(new Paragraph(" ").setFontSize(2));
        leftCell.add(labelSmall("ARRONDISSEMENT\nSUBDIVISION"));
        leftCell.add(valeurRouge("POUMOUGNE"));
        headerTable.addCell(leftCell);

        // Colonne centrale – centre d'état civil
        Cell centerCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(2)
                .setTextAlignment(TextAlignment.CENTER);
        centerCell.add(new Paragraph("CENTRE D'ETAT CIVIL")
                .setFont(fontBold).setFontSize(FONT_SIZE_HEADER)
                .setTextAlignment(TextAlignment.CENTER));
        centerCell.add(new Paragraph("CIVIL STATUS REGISTRATION CENTRE")
                .setFont(fontNormal).setFontSize(FONT_SIZE_SMALL - 1)
                .setTextAlignment(TextAlignment.CENTER));
        centerCell.add(new Paragraph(" ").setFontSize(3));
        centerCell.add(new Paragraph("de - Of")
                .setFont(fontNormal).setFontSize(FONT_SIZE_SMALL)
                .setTextAlignment(TextAlignment.CENTER));
        centerCell.add(new Paragraph(nomMairie)
                .setFont(fontBold).setFontSize(FONT_SIZE_NORMAL + 1)
                .setTextAlignment(TextAlignment.CENTER)
                .setUnderline());
        headerTable.addCell(centerCell);

        // Colonne droite – devise bilingue
        Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0)
                .setTextAlignment(TextAlignment.RIGHT);
        rightCell.add(new Paragraph("REPUBLIQUE DU CAMEROUN")
                .setFont(fontBold).setFontSize(FONT_SIZE_SMALL)
                .setTextAlignment(TextAlignment.RIGHT));
        rightCell.add(new Paragraph("PAIX – TRAVAIL – PATRIE")
                .setFont(fontItalic).setFontSize(FONT_SIZE_SMALL)
                .setTextAlignment(TextAlignment.RIGHT));
        rightCell.add(new Paragraph(" ").setFontSize(4));
        rightCell.add(new Paragraph("REPUBLIC OF CAMEROON")
                .setFont(fontBold).setFontSize(FONT_SIZE_SMALL)
                .setTextAlignment(TextAlignment.RIGHT));
        rightCell.add(new Paragraph("PEACE – WORK – FATHERLAND")
                .setFont(fontItalic).setFontSize(FONT_SIZE_SMALL)
                .setTextAlignment(TextAlignment.RIGHT));
        // Numéro de l'acte (coin supérieur droit)
        String numActe = acteNaissance.getNumeroActe() != null ? acteNaissance.getNumeroActe() : "____";
        rightCell.add(new Paragraph(" ").setFontSize(4));
        rightCell.add(new Paragraph("No " + numActe)
                .setFont(fontBold).setFontSize(FONT_SIZE_NORMAL)
                .setTextAlignment(TextAlignment.RIGHT));
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph(" ").setFontSize(4));

        // Ligne de séparation
        SolidLine solidLine = new SolidLine(0.5f);
        solidLine.setColor(ColorConstants.BLACK);
        document.add(new LineSeparator(solidLine)
                .setWidth(UnitValue.createPercentValue(100)));
        document.add(new Paragraph(" ").setFontSize(3));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // TITRE
    // ═══════════════════════════════════════════════════════════════════════════

    private void ajouterTitre(Document document) throws IOException {
        document.add(new Paragraph("Acte de Naissance")
                .setFont(fontBold)
                .setFontSize(FONT_SIZE_TITLE + 2)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(6));

        document.add(new Paragraph("BIRTH CERTIFICATE")
                .setFont(fontNormal)
                .setFontSize(FONT_SIZE_NORMAL)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(8));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CORPS
    // ═══════════════════════════════════════════════════════════════════════════

    private void ajouterCorps(Document document) throws IOException {

        // Récupération des données
        var declaration  = acteNaissance.getDeclaration();
        var enfant       = declaration != null ? declaration.getEnfant()  : null;
        var mere         = declaration != null ? declaration.getMere()    : null;
        Parent pere         = acteNaissance.getPere();
        var hopital      = declaration != null ? declaration.getHopital() : null;

        String nomEnfant      = enfant  != null ? (enfant.getNom()    + " " + enfant.getPrenom())    : "________________";
        String sexeEnfant     = enfant  != null && enfant.getSexe() != null ? enfant.getSexe().getLibelle() : "________________";
        String lieuNaissance  = hopital != null ? hopital.getNom() : (declaration != null && declaration.getMairie() != null ? declaration.getMairie().getNom() : "________________");
        String dateNaissEnfant= enfant  != null && enfant.getDateNaissance() != null
                ? formaterDateLongue(enfant.getDateNaissance()) : "________________";

        String nomPere        = pere  != null ? (pere.getNom()   + " " + pere.getPrenom())  : "________________";
        String lieuNaissPere  = pere  != null && pere.getLieuNaissance()  != null ? pere.getLieuNaissance()  : "________________";
        String dateNaissPere  = pere  != null && pere.getDateNaissance()  != null ? formaterDate(pere.getDateNaissance()) : "________________";
        String domicilePere   = pere  != null && pere.getDomicile()       != null ? pere.getDomicile()       : "________________";
        String professionPere = pere  != null && pere.getProfession()     != null ? pere.getProfession()     : "________________";

        String nomMere        = mere  != null ? (mere.getNom()   + " " + mere.getPrenom())  : "________________";
        String lieuNaissMere  = mere  != null && mere.getLieuNaissance()  != null ? mere.getLieuNaissance()  : "________________";
        String dateNaissMere  = mere  != null && mere.getDateNaissance()  != null ? formaterDate(mere.getDateNaissance()) : "________________";
        String domicileMere   = mere  != null && mere.getDomicile()       != null ? mere.getDomicile()       : "________________";
        String professionMere = mere  != null && mere.getProfession()     != null ? mere.getProfession()     : "________________";

        String dateDeclaration = declaration != null && declaration.getDate() != null
                ? formaterDate(declaration.getDate()) : "________________";
        String dateActe = acteNaissance.getDate() != null ? formaterDate(acteNaissance.getDate()) : "________________";

        // ── Nom de l'enfant ──────────────────────────────────────────────────
        ajouterLigneChamp(document,
                "Nom de l'enfant.",
                "Name of the child",
                nomEnfant);

        // ── Date et lieu de naissance ────────────────────────────────────────
        Table dateTable = new Table(UnitValue.createPercentArray(new float[]{55, 45}))
                .setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);

        Cell dateCell = new Cell().setBorder(Border.NO_BORDER).setPadding(1);
        dateCell.add(new Paragraph()
                .add(new Text("Le - On the ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(dateNaissEnfant).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline()));
        dateTable.addCell(dateCell);

        Cell lieuCell = new Cell().setBorder(Border.NO_BORDER).setPadding(1);
        lieuCell.add(new Paragraph()
                .add(new Text("Est né à - Was born at ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(lieuNaissance).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline()));
        dateTable.addCell(lieuCell);
        document.add(dateTable);

        // ── Nom de l'enfant (répété) ─────────────────────────────────────────
        ajouterLigneChamp(document,
                "Nom de l'enfant.",
                "Name of the child",
                nomEnfant);

        // ── Sexe ─────────────────────────────────────────────────────────────
        ajouterLigneSimple(document, "De sexe - Sex.", sexeEnfant);

        // ═══ PÈRE ═══════════════════════════════════════════════════════════
        document.add(new Paragraph(" ").setFontSize(3));
        ajouterLigneChamp(document, "De - Of.", "(Père / Father)", nomPere);
        ajouterLigneDouble(document,
                "Né à - Born at.", lieuNaissPere,
                "Le - On the.", dateNaissPere);
        ajouterLigneDouble(document,
                "Domicilié à - Resident at.", domicilePere,
                "Profession - Occupation.", professionPere);

        // ═══ MÈRE ════════════════════════════════════════════════════════════
        document.add(new Paragraph(" ").setFontSize(3));
        ajouterLigneChamp(document, "Et de - And of.", "(Mère / Mother)", nomMere);
        ajouterLigneDouble(document,
                "Née à - Born at.", lieuNaissMere,
                "Le - On the.", dateNaissMere);
        ajouterLigneDouble(document,
                "Domiciliée à - Resident at.", domicileMere,
                "Profession - Occupation.", professionMere);

        // ── Date de dressage de l'acte ───────────────────────────────────────
        document.add(new Paragraph(" ").setFontSize(3));
        ajouterLigneSimple(document, "Dressé le - Drawn up at.", dateActe);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SECTION DÉCLARATION
    // ═══════════════════════════════════════════════════════════════════════════

    private void ajouterSectionDeclaration(Document document) throws IOException {
        var declaration = acteNaissance.getDeclaration();
        String hopitalNom = declaration != null && declaration.getHopital() != null
                ? declaration.getHopital().getNom() : "________________";
        String dateDecl = declaration != null && declaration.getDate() != null
                ? formaterDate(declaration.getDate()) : "________________";

        document.add(new Paragraph(" ").setFontSize(4));

        Paragraph declPara = new Paragraph()
                .add(new Text("Sur la déclaration de - In accordance with the declaration of ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(dateDecl).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline())
                .add(new Text("  de la Maternité / Hôpital de - of the Hospital of  ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(hopitalNom).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline());
        document.add(declPara);

        document.add(new Paragraph(" ").setFontSize(2));
        document.add(new Paragraph("Lesquels ont certifié la sincérité de la présente déclaration.")
                .setFont(fontItalic).setFontSize(FONT_SIZE_NORMAL));
        document.add(new Paragraph("Who attested to the truth of this declaration.")
                .setFont(fontItalic).setFontSize(FONT_SIZE_SMALL));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SIGNATURES
    // ═══════════════════════════════════════════════════════════════════════════

    private void ajouterSignatures(Document document) throws IOException {
        String nomMairie = acteNaissance.getDeclaration() != null
                && acteNaissance.getDeclaration().getMairie() != null
                ? acteNaissance.getDeclaration().getMairie().getNom()
                : "________________";

        document.add(new Paragraph(" ").setFontSize(6));

        // "Par nous … Officier"
        Paragraph parNous = new Paragraph()
                .add(new Text("Par nous - By us ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text("___________________________________").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text("  Officier").setFont(fontBold).setFontSize(FONT_SIZE_NORMAL));
        document.add(parNous);

        Paragraph deEtatCivil = new Paragraph()
                .add(new Text("de l'État Civil du Centre de - of the Civil Status Centre of  ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(nomMairie).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline());
        document.add(deEtatCivil);

        document.add(new Paragraph(" ").setFontSize(3));

        // "Assisté de …"
        Paragraph assisteDe = new Paragraph()
                .add(new Text("Assisté de - In the presence of ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text("___________________________________").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL));
        document.add(assisteDe);

        Paragraph secretaire = new Paragraph()
                .add(new Text("Secrétaire d'état civil - Civil Status Registrar.").setFont(fontItalic).setFontSize(FONT_SIZE_SMALL));
        document.add(secretaire);

        document.add(new Paragraph(" ").setFontSize(14));

        // Tableau signatures
        Table sigTable = new Table(UnitValue.createPercentArray(new float[]{40, 20, 40}))
                .setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);

        Cell leftSig = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        leftSig.add(new Paragraph("Le Secrétaire\nSecretary")
                .setFont(fontNormal).setFontSize(FONT_SIZE_SMALL).setTextAlignment(TextAlignment.CENTER));
        leftSig.add(new Paragraph("\n\n\n___________________________")
                .setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL));
        sigTable.addCell(leftSig);

        sigTable.addCell(new Cell().setBorder(Border.NO_BORDER));

        Cell rightSig = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        rightSig.add(new Paragraph("Signature de l'Officier d'état civil\nSignature of Civil Status Registrar")
                .setFont(fontNormal).setFontSize(FONT_SIZE_SMALL).setTextAlignment(TextAlignment.CENTER));
        rightSig.add(new Paragraph("\n\n\n___________________________")
                .setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL).setTextAlignment(TextAlignment.CENTER));
        rightSig.add(new Paragraph("L'OFFICIER")
                .setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setTextAlignment(TextAlignment.CENTER));
        sigTable.addCell(rightSig);

        document.add(sigTable);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS DE MISE EN PAGE
    // ═══════════════════════════════════════════════════════════════════════════

    /** Ligne label bilingue + valeur soulignée sur toute la largeur */
    private void ajouterLigneChamp(Document document, String labelFr, String labelEn, String valeur) throws IOException {
        Paragraph p = new Paragraph()
                .add(new Text(labelFr + " ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(valeur + "  ").setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline())
                .add(new Text(labelEn).setFont(fontItalic).setFontSize(FONT_SIZE_SMALL - 1));
        document.add(p);
    }

    /** Ligne simple : label + valeur */
    private void ajouterLigneSimple(Document document, String label, String valeur) throws IOException {
        Paragraph p = new Paragraph()
                .add(new Text(label + " ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(valeur).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline());
        document.add(p);
    }

    /** Deux champs côte à côte sur la même ligne */
    private void ajouterLigneDouble(Document document,
                                    String label1, String val1,
                                    String label2, String val2) throws IOException {
        Table t = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);

        Cell c1 = new Cell().setBorder(Border.NO_BORDER).setPadding(1);
        c1.add(new Paragraph()
                .add(new Text(label1 + " ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(val1).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline()));
        t.addCell(c1);

        Cell c2 = new Cell().setBorder(Border.NO_BORDER).setPadding(1);
        c2.add(new Paragraph()
                .add(new Text(label2 + " ").setFont(fontNormal).setFontSize(FONT_SIZE_NORMAL))
                .add(new Text(val2).setFont(fontBold).setFontSize(FONT_SIZE_NORMAL).setUnderline()));
        t.addCell(c2);

        document.add(t);
    }

    // ── Petits helpers typographiques ────────────────────────────────────────

    private Paragraph labelSmall(String text) throws IOException {
        return new Paragraph(text)
                .setFont(fontBold).setFontSize(FONT_SIZE_SMALL)
                .setMultipliedLeading(1.1f).setMargin(0);
    }

    private Paragraph valeurRouge(String text) throws IOException {
        return new Paragraph(text)
                .setFont(fontBold).setFontSize(FONT_SIZE_NORMAL)
                .setFontColor(ColorConstants.RED)
                .setMultipliedLeading(1.1f).setMargin(0);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FORMATAGE DES DATES
    // ═══════════════════════════════════════════════════════════════════════════

    private String formaterDate(LocalDate date) {
        if (date == null) return "________________";
        return date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH));
    }

    /** Ex : "huit Janvier mil neuf cent quatre-vingt-seize" */
    private String formaterDateLongue(LocalDate date) {
        if (date == null) return "________________";
        // On retourne le format court ; pour la version lettre il faudrait une librairie spécialisée
        return "le " + date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH));
    }
}