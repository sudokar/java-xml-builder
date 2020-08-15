package io.github.sudokar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;

import static java.util.Objects.requireNonNull;

public final class XmlBuilder {
  private static final String DEFAULT_ENCODING = "UTF-8";
  private final Document document;
  private final Deque<Element> stack;

  private Element currentElement;
  private String encoding;

  public XmlBuilder() {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;
    try {
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e.getMessage());
    }
    this.document = documentBuilder.newDocument();
    this.stack = new ArrayDeque<>();
  }

  public PreBuildStage root(String tagName, String encoding) {
    requireNonNull(tagName, "XML root tag name must not be null");
    requireNonNull(encoding, "XML encoding tag name must not be null");
    Element element = this.document.createElement(tagName);
    this.stack.push(element);
    this.encoding = encoding;
    return new PreBuildStage();
  }

  public PreBuildStage root(String tagName) {
    return this.root(tagName, DEFAULT_ENCODING);
  }

  private Document getDocument() {
    document.appendChild(stack.pop());
    return document;
  }

  class PreBuildStage {

    public PreBuildStage element(String tagName) {
      return this.element(tagName, null);
    }

    public <T> PreBuildStage element(String tagName, T value) {
      Element element = document.createElement(tagName);
      if (value != null) {
        element.appendChild(document.createTextNode(value.toString()));
      }
      currentElement = element;
      stack.peek().appendChild(element);
      return this;
    }

    public ElementGroupStage beginElementGroup(String tagName) {
      Element element = document.createElement(tagName);
      stack.push(element);
      currentElement = null;
      return new ElementGroupStage(this);
    }

    public <T> PreBuildStage withAttribute(String name, T value) {
      requireNonNull(name, "XML tag attribute name must not be null");
      requireNonNull(value, "XML tag attribute value must not be null");
      Element elementToUpdate = (currentElement == null) ? stack.peek() : currentElement;
      elementToUpdate.setAttribute(name, value.toString());
      return this;
    }

    public PreBuildStage withNamespace(String prefix, String uri) {
      requireNonNull(uri, "Namespace URI must not be null");
      Element elementToUpdate = (currentElement == null) ? stack.peek() : currentElement;
      if (prefix == null) {
        elementToUpdate.setAttribute("xmlns:", uri);
      } else {
        elementToUpdate.setAttribute("xmlns:" + prefix, uri);
      }
      return this;
    }

    public PreBuildStage withNamespace(String uri) {
      return this.withNamespace(null, uri);
    }

    public PostBuildStage build() {
      return new PostBuildStage();
    }

  }

  class ElementGroupStage {
    private final PreBuildStage preBuildStage;

    public ElementGroupStage(PreBuildStage preBuildStage) {
      this.preBuildStage = preBuildStage;
    }

    public ElementGroupStage beginElementGroup(String name) {
      requireNonNull(name, "XML tag name must not be null");
      Element element = document.createElement(name);
      stack.push(element);
      currentElement = null;
      return this;
    }

    public ElementGroupStage element(String name) {
      requireNonNull(name, "XML tag name must not be null");
      preBuildStage.element(name);
      return this;
    }

    public <T> ElementGroupStage element(String name, T value) {
      requireNonNull(name, "XML tag name must not be null");
      requireNonNull(value, "XML tag value must not be null");
      preBuildStage.element(name, value.toString());
      return this;
    }

    public <T> ElementGroupStage withAttribute(String name, T value) {
      preBuildStage.withAttribute(name, value.toString());
      return this;
    }

    public ElementGroupStage withNamespace(String prefix, String uri) {
      preBuildStage.withNamespace(prefix, uri);
      return this;
    }

    public ElementGroupStage withNamespace(String uri) {
      preBuildStage.withNamespace(uri);
      return this;
    }

    public ElementGroupStage endElementGroup() {
      Element element = stack.pop();
      stack.peek().appendChild(element);
      return this;
    }

    public PostBuildStage build() {
      return new PostBuildStage();
    }
  }

  class PostBuildStage {
    public String getXml() {
      try {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("encoding", encoding);
        return this.transformToXmlString(transformer);
      } catch (TransformerConfigurationException e) {
        throw new RuntimeException(e.getMessage());
      }
    }

    public String getPrettyXml() {
      try {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty("encoding", encoding);
        return this.transformToXmlString(transformer);
      } catch (TransformerConfigurationException e) {
        throw new RuntimeException(e.getMessage());
      }
    }

    private String transformToXmlString(Transformer transformer) {
      try {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        DOMSource source = new DOMSource(getDocument());
        transformer.transform(source, result);
        return writer.toString();
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }
}
