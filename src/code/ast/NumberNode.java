package code.ast;

import code.visitor.ValueVisitor;

public class NumberNode extends ValueNode {

    String value;
    protected NumberNode(String value) {
        super(ValueKind.NUMBER);
        this.value = value;
    }

    @Override
    public ValueNode accept(ValueVisitor visitor) {
        return visitor.visit(this);
    }
}
