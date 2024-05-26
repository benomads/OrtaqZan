package benomads.ortaq_zan.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.*;

public class DocumentLoaderExamples {

    private static final Logger log = LoggerFactory.getLogger(DocumentLoaderExamples.class);

    public static void main(String[] args) {
        loadSingleDocument();
        loadMultipleDocuments();
        loadMultipleDocumentsWithGlob();
        loadMultipleDocumentsRecursively();
        loadUsingParserFromSPI();
    }

    public static void loadSingleDocument() {
        Path documentPath = toPath("data/same-test-2.txt");
        log.info("Loading single document: {}", documentPath);
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());
        log(document);
        log.info("");
    }

    public static void loadMultipleDocuments() {
        Path directoryPath = toPath("data/");
        log.info("Loading multiple documents from: {}", directoryPath);
        List<Document> documents = loadDocuments(directoryPath, new ApacheTikaDocumentParser());
        documents.forEach(DocumentLoaderExamples::log);
        log.info("");
    }

    public static void loadMultipleDocumentsWithGlob() {
        Path directoryPath = toPath("data/");
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        log.info("Loading *.txt documents from: {}", directoryPath);
        List<Document> documents = loadDocuments(directoryPath, pathMatcher, new ApacheTikaDocumentParser());
        documents.forEach(DocumentLoaderExamples::log);
        log.info("");
    }

    public static void loadMultipleDocumentsRecursively() {
        Path directoryPath = toPath("data/");
        log.info("Loading multiple documents recursively from: {}", directoryPath);
        List<Document> documents = loadDocumentsRecursively(directoryPath, new ApacheTikaDocumentParser());
        documents.forEach(DocumentLoaderExamples::log);
        log.info("");
    }

    public static void loadUsingParserFromSPI() {
        Path documentPath = toPath("data/same-test-2.txt");
        log.info("Loading using parser imported through SPI: {}", documentPath);
        Document document = loadDocument(documentPath); // we are not specifying a parser here, it is imported through SPI
        log(document);
        log.info("");
    }

    private static void log(Document document) {
        log.info("{}: {} ...", document.metadata("file_name"), document.text().trim().substring(0, 50));
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = DocumentLoaderExamples.class.getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
