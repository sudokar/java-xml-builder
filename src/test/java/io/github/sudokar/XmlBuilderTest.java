package io.github.sudokar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlBuilderTest {

  private XmlBuilder xmlBuilder;

  @BeforeEach
  void setUp() {
    xmlBuilder = new XmlBuilder();
  }

  @Test
  void shouldBuildXmlWhenOnlyRootTagIsSet() {
    // Act
    final String xml =
        xmlBuilder
            .root("orders")
            .build()
            .getXml();
    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><orders/>");
  }

  @Test
  void shouldThrowExceptionWhenRootTagNameIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> xmlBuilder.root(null));
  }

  @Test
  void shouldBuildXmlWhenEncodingIsSet() {
    // Act
    final String xml =
        xmlBuilder
            .root("orders", "UTF-16")
            .build()
            .getXml();
    ;

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"no\"?><orders/>");
  }

  @Test
  void shouldThrowExceptionWhenEncodingIsNull() {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> xmlBuilder.root("orders", null));
  }

  @Test
  void shouldBuildXmlWithChildElements() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("note")
              .element("to", "Tove")
              .element("from", "Jani")
              .element("heading", "Reminder")
              .element("body", "Don't forget me this weekend!")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<note>" +
            "<to>Tove</to>" +
            "<from>Jani</from>" +
            "<heading>Reminder</heading>" +
            "<body>Don't forget me this weekend!</body>" +
            "</note>");
  }

  @Test
  void shouldBuildXmlWithElementGroups() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("breakfast_menu")
              .beginElementGroup("food")
                .element("name", "Belgian Waffles")
                .element("price", "$5.95")
                .element("description", "Two of our famous Belgian Waffles with plenty of real maple syrup")
                .element("calories", 650)
              .endElementGroup()
              .beginElementGroup("food")
                .element("name", "Strawberry Belgian Waffles")
                .element("price", "$7.95")
                .element("description", "Light Belgian waffles covered with strawberries and whipped cream")
                .element("calories", 900)
              .endElementGroup()
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<breakfast_menu>" +
            "<food>" +
            "<name>Belgian Waffles</name>" +
            "<price>$5.95</price>" +
            "<description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>" +
            "<calories>650</calories>" +
            "</food>" +
            "<food>" +
            "<name>Strawberry Belgian Waffles</name>" +
            "<price>$7.95</price>" +
            "<description>Light Belgian waffles covered with strawberries and whipped cream</description>" +
            "<calories>900</calories>" +
            "</food>" +
            "</breakfast_menu>");
  }

  @Test
  void shouldBuildXmlWithNestedElementGroups() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("breakfast_menu")
              .beginElementGroup("food")
                .element("name", "Belgian Waffles")
                .beginElementGroup("price")
                  .element("money_symbol", "$")
                  .element("amount", "5.96")
                .endElementGroup()
                .element("description", "Two of our famous Belgian Waffles with plenty of real maple syrup")
                .element("calories", 650)
              .endElementGroup()
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<breakfast_menu>" +
            "<food>" +
            "<name>Belgian Waffles</name>" +
            "<price>" +
            "<money_symbol>$</money_symbol>" +
            "<amount>5.96</amount>" +
            "</price>" +
            "<description>Two of our famous Belgian Waffles with plenty of real maple syrup</description>" +
            "<calories>650</calories>" +
            "</food>" +
            "</breakfast_menu>");
  }

  @Test
  void shouldAddNamespaceToRootTag() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("contact")
            .withNamespace("https://github.com/sudokar")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<contact xmlns=\"https://github.com/sudokar\"/>");
  }

  @Test
  void shouldAddNamespaceWithPrefixToRootTag() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("cont:contact")
            .withNamespace("cont", "https://github.com/sudokar")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<cont:contact xmlns:cont=\"https://github.com/sudokar\"/>");
  }

  @Test
  void shouldAddNamespaceToElementGroup() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("root")
              .beginElementGroup("contact")
              .withNamespace("https://github.com/sudokar")
              .endElementGroup()
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<root>" +
            "<contact xmlns=\"https://github.com/sudokar\"/>" +
            "</root>");
  }

  @Test
  void shouldAddNamespaceWithPrefixToElementGroup() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("root")
            .beginElementGroup("cont:contact")
              .withNamespace("cont", "https://github.com/sudokar")
            .endElementGroup()
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<root>" +
            "<cont:contact xmlns:cont=\"https://github.com/sudokar\"/>" +
            "</root>");
  }

  @Test
  void shouldAddNamespaceToElement() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("contact")
            .withNamespace("https://github.com/sudokar/xml-builder")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<contact xmlns=\"https://github.com/sudokar/xml-builder\"/>");
  }

  @Test
  void shouldAddNamespaceWithPrefixToElement() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("contact")
              .element("github:profile")
              .withNamespace("github", "https://github.com/sudokar/xml-builder")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<contact>" +
            "<github:profile xmlns:github=\"https://github.com/sudokar/xml-builder\"/>" +
            "</contact>");
  }

  @Test
  void shouldBuildXmlWithAttributeOnRootElement() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("profile")
            .withAttribute("name", "sudokar")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<profile name=\"sudokar\"/>");
  }

  @Test
  void shouldBuildXmlWithAttributeToElement() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("profile")
              .element("contact")
              .withAttribute("name", "sudokar")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<profile>" +
            "<contact name=\"sudokar\"/>" +
            "</profile>");
  }

  @Test
  void shouldBuildXmlWithAttributeToElementGroup() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("profile")
              .beginElementGroup("contact")
              .withAttribute("id", "12345")
                .element("name", "sudokar")
              .endElementGroup()
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<profile>" +
            "<contact id=\"12345\">" +
            "<name>sudokar</name>" +
            "</contact>" +
            "</profile>");
  }

  @Test
  void shouldBuildXmlWithEmptyElementGroup() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("profile")
            .beginElementGroup("contact")
            .endElementGroup()
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<profile>" +
            "<contact/>" +
            "</profile>");
  }

  @Test
  void shouldBuildXmlWithEmptyElement() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("profile")
              .element("contact")
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<profile>" +
            "<contact/>" +
            "</profile>");
  }

  @Test
  void shouldBuildXmlWithEmptyElementWithinElementGroup() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("profile")
            .beginElementGroup("contact")
              .element("name")
            .endElementGroup()
            .build()
            .getXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
            "<profile>" +
            "<contact>" +
            "<name/>" +
            "</contact>" +
            "</profile>");
  }

  @Test
  void shouldGetPrettyXml() {
    // Act
    // @formatter:off
    final String xml =
        xmlBuilder
            .root("breakfast_menu")
              .withNamespace("https://github.com/sudokar")
              .withAttribute("style", "american")
              .beginElementGroup("food")
                .withAttribute("type", "sweet")
                .element("name", "Belgian Waffles")
                .withAttribute("group", "waffles")
                .beginElementGroup("price")
                  .element("money_symbol", "$")
                  .element("amount", "5.96")
                .endElementGroup()
                .element("description", "Two of our famous Belgian Waffles with plenty of real maple syrup")
                .element("calories", 650)
              .endElementGroup()
            .build()
            .getPrettyXml();
    // @formatter:on

    // Assert
    assertThat(xml)
        .startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
  }
}