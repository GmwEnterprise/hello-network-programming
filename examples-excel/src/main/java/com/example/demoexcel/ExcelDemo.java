package com.example.demoexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedList;
import java.util.Objects;

public class ExcelDemo {

    public static class Struct {
        private String node, parent, type, value;

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("node=%-30s, parent=%-30s, type=%-10s",
                                 node, parent, type);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class StackElement {
        public String key;
        public String type;
        public JsonNode node;

        public StackElement(String key, JsonNode node) {
            this.key = key;
            this.type = node instanceof ArrayNode ? "array" : "object";
            this.node = node;
        }

        public ObjectNode getNode() {
            if (node.getNodeType().equals(JsonNodeType.OBJECT)) {
                return (ObjectNode) node;
            }
            return (ObjectNode) node.get(0);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StackElement that = (StackElement) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    public static void main(String[] args) {

        // ObjectNode root = new ObjectNode(JsonNodeFactory.instance);
        LinkedList<StackElement> stack = new LinkedList<>();
        stack.add(new StackElement("UNI_BSS_BODY", new ObjectNode(JsonNodeFactory.instance)));

        String path = "C:\\Users\\Gmw\\Desktop\\新建 Microsoft Excel 工作表.xlsx";
        EasyExcel.read(path, Struct.class, new AnalysisEventListener<Struct>() {

            @Override
            public void invoke(Struct row, AnalysisContext context) {
                System.out.printf("[%S] ==> %s%n", Thread.currentThread(), row);

                while (!Objects.equals(row.parent, stack.getLast().key)) {
                    stack.removeLast();
                }
                if ("object".equals(row.type)) {
                    ObjectNode node = stack.getLast().getNode().putObject(row.node);
                    stack.add(new StackElement(row.node, node));
                } else if ("array".equals(row.type)) {
                    ArrayNode array = stack.getLast().getNode().putArray(row.node);
                    array.addObject();
                    stack.add(new StackElement(row.node, array));
                } else {
                    ObjectNode obj = stack.getLast().getNode().putObject(row.node);
                    obj.put("type", row.type);
                    obj.put("description", row.value);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                System.out.println(stack.getFirst().getNode().toString());
            }
        }).sheet().doRead();
    }
}
