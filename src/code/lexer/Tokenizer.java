package code.lexer;

import code.ast.Utility;

import java.util.ArrayList;

interface GenericLexer {
    boolean advance();
    void lex();
    void lexString();
    void lexNumber();

}
public class Tokenizer implements GenericLexer {

    private int idx;
    private ArrayList<Token> tokens;
    private char currentChar;
    private  String src;
    private boolean lexerError;

    public Tokenizer(String path) {
        src = BufferReader.read(path);
        tokens = new ArrayList<>();
        advance();
        lex();
    }

    @Override
    public boolean advance() {
        if (idx < src.length()) {
            currentChar = src.charAt(idx++);
            return true;
        }
        currentChar = '\0';
        return false;
    }

    @Override
    public void lex() {
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                advance();
            } else if (currentChar == '"') { // start of string
                advance(); // "l
                var buffer = new StringBuilder();
                while (currentChar != '"' && currentChar != '\0') {
                    buffer.append(currentChar);
                    advance();
                }
                if (currentChar == '"') {
                    tokens.add(new Token(Token.TokenType.STRING, buffer.toString()));
                    advance();
                } else {
                    lexerError = true;
                    break;
                }

            }
            else if (currentChar == '{') {
                tokens.add(new Token(Token.TokenType.L_BRACE, "{"));
                advance();
            } else if (currentChar == '}') {
                tokens.add(new Token(Token.TokenType.R_BRACE, "}"));
                advance();
            } else if (Character.isJavaIdentifierStart(currentChar)) {
                lexString();
            } else if (Character.isDigit(currentChar)) {
                lexNumber();
            } else if (currentChar == ':') {
                tokens.add(new Token(Token.TokenType.COLON, ":"));
                advance();
            } else if (currentChar == ',') {
                tokens.add(new Token(Token.TokenType.COMMA, ","));
                advance();
            } else {
                lexerError = true;
                advance();
                break;
            }
        }

    }

    @Override
    public void lexString() {
        final var buffer = new StringBuffer();
        while (Character.isJavaIdentifierPart(currentChar) && currentChar != '\0') {
            buffer.append(currentChar);
            advance();
        }
        if (Utility.isBoolString(buffer.toString()))
            tokens.add(new Token(Token.TokenType.BOOLEAN, buffer.toString()));
        else if (Utility.isNullString(buffer.toString()))
            tokens.add(new Token(Token.TokenType.NULL, buffer.toString()));  // FIXME
    }

    @Override
    public void lexNumber() {
        final var buffer = new StringBuffer();
        while (Character.isDigit(currentChar) && currentChar != '\0') {
            buffer.append(currentChar);
            advance();
        }
        tokens.add(new Token(Token.TokenType.NUMBER, buffer.toString()));
    }


    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public static void main(String[] args) {
        var lexer = new Tokenizer("{\"bool");
        System.out.println(lexer.tokens);
    }
}
