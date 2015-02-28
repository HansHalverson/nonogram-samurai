import java.io.*;

import javax.xml.parsers.*;
import javax.xml.stream.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class NonogramXML {
	
	public static void saveNonogramXML(NonogramBoard nonogram, String name) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			File xmlFile = new File("SavedNonograms.xml");
			if (!xmlFile.exists()) {
				FileOutputStream xmlWriter = new FileOutputStream(xmlFile);
				XMLOutputFactory xmlOutputFact = XMLOutputFactory.newInstance();
				XMLStreamWriter xmlStreamWriter = xmlOutputFact.createXMLStreamWriter(xmlWriter);
				xmlStreamWriter.writeStartDocument();
				xmlStreamWriter.writeStartElement("savednonograms");
				xmlStreamWriter.writeEndElement();
				xmlWriter.close();
			}
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc =  docBuilder.parse(xmlFile);
			Element root = doc.getDocumentElement();
			NodeList nonograms = root.getChildNodes();
			boolean existsAlready = false;
			for (int i = 0; i < nonograms.getLength(); i++) {
				Node node = nonograms.item(i);
				if (node.getAttributes() != null) {
					existsAlready = existsAlready || (node.getAttributes().item(0).getNodeValue().equals(name));
				}
			}
			if (!existsAlready) {
				Element newNonogram = doc.createElement("nonogram");
				newNonogram.setAttribute("name", name);
				Element size = doc.createElement("size");
				size.setTextContent(String.valueOf(nonogram.getSize()));
				newNonogram.appendChild(size);
				for (int i = 0; i < nonogram.getSize(); i++) {
					Element line = doc.createElement("line" + (i + 1));
					String lineValue = "";
					for (int j = 0; j < nonogram.getSize(); j++) {
						if (nonogram.getBoard()[i][j] == NonogramBoard.TileStatus.FILLED) {
							lineValue += "1";
						} else {
							lineValue += "0";
						}
					}
					line.setTextContent(lineValue);
					newNonogram.appendChild(line);
				}
				root.appendChild(newNonogram);
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("SavedNonograms.xml")));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
	}
	
	public static NonogramBoard getSavedNonogram(String name) {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			Document doc = docBuilderFactory.newDocumentBuilder().parse("SavedNonograms.xml");
			NodeList nonograms = doc.getDocumentElement().getChildNodes();
			for (int i = 1; i < nonograms.getLength(); i += 2) {
				if (nonograms.item(i).hasAttributes() && 
						nonograms.item(i).getAttributes().item(0).getNodeValue().equals(name)) {
					NodeList savedNonogramProperties = nonograms.item(i).getChildNodes();
					int boardSize = Integer.parseInt(savedNonogramProperties.item(1).getFirstChild().getNodeValue());
					NonogramBoard.TileStatus[][] board = new NonogramBoard.TileStatus[boardSize][boardSize];
					for (int j = 3; j < savedNonogramProperties.getLength(); j += 2) {
						String line = savedNonogramProperties.item(j).getFirstChild().getNodeValue();
						for (int k = 0; k < line.length(); k++) {
							if (line.charAt(k) == '0') {
								board[(j - 3) / 2][k] = NonogramBoard.TileStatus.EMPTY;
							} else {
								board[(j - 3) / 2][k] = NonogramBoard.TileStatus.FILLED;
							}
						}
					}
					return new NonogramBoard(board);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
		return null;
	}
	
	public static void main(String[] args) {
		saveNonogramXML(new NonogramBoard (new NonogramBoard.TileStatus[][]{{NonogramBoard.TileStatus.FILLED}}), "Empty");
		NonogramBoard nono = getSavedNonogram("Test");
		new BoardView(new BoardModel(nono));
	}
}
