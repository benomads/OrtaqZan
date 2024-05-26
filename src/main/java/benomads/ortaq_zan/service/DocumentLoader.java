package benomads.ortaq_zan.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.*;

@Service
public class DocumentLoader {
    private static final Logger log = LoggerFactory.getLogger(DocumentLoader.class);

    private final ResourceLoader resourceLoader;

    public DocumentLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    public static void loadSingleDocument() {
        Path documentPath = toPath("data/same-test-2.txt");
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());

//        var resource = resourceLoader.getResource(documentPath);
//        var termsOfUse = loadDocument(resource.getFile().toPath(), new TextDocumentParser());
    }

    public static List<Document> loadMultipleDocuments() {
        Path directoryPath = toPath("data/");

        return loadDocuments(directoryPath, new ApacheTikaDocumentParser());

    }

    public static void loadMultipleDocumentsWithGlob() {
        Path directoryPath = toPath("data/");
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        List<Document> documents = loadDocuments(directoryPath, pathMatcher, new ApacheTikaDocumentParser());
    }

    public static void loadUsingParserFromSPI() {
        Path documentPath = toPath("example-files/story-about-happy-carrot.pdf");
        log.info("Loading using parser imported through SPI: {}", documentPath);
        Document document = loadDocument(documentPath); // we are not specifying a parser here, it is imported through SPI
        log.info("");
    }


    private static void loadMultipleDocumentsRecursively() {
        Path directoryPath = toPath("data/");
        log.info("Loading multiple documents recursively from: {}", directoryPath);
        List<Document> documents = loadDocumentsRecursively(directoryPath, new ApacheTikaDocumentParser());

    }


    private static Path toPath(String fileName) {
        try {
            URL fileUrl = DocumentLoaderExamples.class.getClassLoader().getResource(fileName);
            assert fileUrl != null;
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
//
//    private static void log(Document document) {
//        log.info("{}: {} ...", document.metadata("file_name"), document.text().trim().substring(0, 50));
//    }


    
}
