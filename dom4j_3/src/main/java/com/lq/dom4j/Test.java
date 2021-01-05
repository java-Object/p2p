package com.lq.dom4j;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * @author Luo
 * @date 2020/12/22 19:23
 * @Package com.lq.dom4j
 * class    dom4j：读取xml文件的内容
 */
public class Test {

    public static void main(String[] args) throws DocumentException {
        String xmlString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "\n" +
                "<bookstore>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Harry Potter</title>\n" +
                "  <price>29.99</price>\n" +
                "</book>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Learning XML</title>\n" +
                "  <price>39.95</price>\n" +
                "</book>\n" +
                "\n" +
                "</bookstore>";

        //获取xml中第一个title的内容
        Document document = DocumentHelper.parseText(xmlString);
        //使用xpath路径表达式  1开始
        Node node = document.selectSingleNode("bookstore/book[2]/title");
        String text = node.getText();
        System.out.println(text);

    }
}
