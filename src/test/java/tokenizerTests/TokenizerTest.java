package tokenizerTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import Tree.Node;
import Tree.Tree;
import parser.Parser;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;

public class TokenizerTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void emptyFunctionParse() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("func main(long a, long b, string c){}");
		Parser parser = new Parser(tokenizer);
		parser.parse();
		HashMap<String, Tree<Token>> functions = parser.getFunctions();
		Tree<Token> mainFunction = functions.get("main");
		Assert.assertEquals("func", mainFunction.getData().getValue());
		Node<Token> main = mainFunction.getChildren().get(0);
		Assert.assertEquals("main", main.getData().getValue());
		ArrayList<Node<Token>> children = main.getChildren();
		Assert.assertEquals("long", children.get(0).getData().getValue());
		Assert.assertEquals("long", children.get(1).getData().getValue());
		Assert.assertEquals("string", children.get(2).getData().getValue());
		Assert.assertEquals("a", children.get(0).getChildren().get(0).getData().getValue());
		Assert.assertEquals("b", children.get(1).getChildren().get(0).getData().getValue());
		Assert.assertEquals("c", children.get(2).getChildren().get(0).getData().getValue());
	}

	@Test
	public void parseSimpleIf() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("if(a){})");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseIf(node);
		Node<Token> ifek = node.getChildren().get(0);
		Node<Token> cond = ifek.getChildren().get(0);
		Node<Token> a = cond.getChildren().get(0);
		
		
		Assert.assertEquals("a", a.getData().getValue());
		
		
	}
	@Test
	public void parseIf() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("if(a & b | c){})");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseIf(node);
		Node<Token> ifek = node.getChildren().get(0);
		Node<Token> cond = ifek.getChildren().get(0);
		Node<Token> or = cond.getChildren().get(0);
		Node<Token> and = or.getChildren().get(0);
		Node<Token> a = and.getChildren().get(0);
		Node<Token> b = and.getChildren().get(1);
		Node<Token> c = or.getChildren().get(1);

		
		Assert.assertEquals("c", c.getData().getValue());
		Assert.assertEquals("b", b.getData().getValue());
		Assert.assertEquals("a", a.getData().getValue());

		
	}
	@Test
	public void parseIfWithDoubleBracket() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("if(a & (b & (c | d | e))){})");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseIf(node);
		Node<Token> ifek = node.getChildren().get(0);
		Node<Token> cond = ifek.getChildren().get(0);
		Node<Token> and = cond.getChildren().get(0);
		Node<Token> a = and.getChildren().get(0);
		Node<Token> andBr = and.getChildren().get(1);
		Node<Token> b = andBr.getChildren().get(0);
		Node<Token> orBrd = andBr.getChildren().get(1);
		Node<Token> c = orBrd.getChildren().get(0);		
		Node<Token> d = orBrd.getChildren().get(1);		
		Node<Token> e = orBrd.getChildren().get(2);		
		
		
		Assert.assertEquals("c", c.getData().getValue());
		Assert.assertEquals("b", b.getData().getValue());
		Assert.assertEquals("a", a.getData().getValue());
		Assert.assertEquals("d", d.getData().getValue());
		Assert.assertEquals("e", e.getData().getValue());
	}
	@Test
	public void parseIfWithBracket() {
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("if(a & (b | c)){};");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseIf(node);
		Node<Token> ifek = node.getChildren().get(0);
		Node<Token> cond = ifek.getChildren().get(0);
		Node<Token> and = cond.getChildren().get(0);
		Node<Token> a = and.getChildren().get(0);
		Node<Token> orBr = and.getChildren().get(1);
		Node<Token> b = orBr.getChildren().get(0);
		Node<Token> c = orBr.getChildren().get(1);
		
		Assert.assertEquals("c", c.getData().getValue());
		Assert.assertEquals("b", b.getData().getValue());
		Assert.assertEquals("a", a.getData().getValue());
	}

	@Test
	public void tokenizeTest() {
		Tokenizer tokenizer = new Tokenizer();
		ArrayList<Token> tokens = tokenizer.tokenizeString("if(1==1){long w=\"siema\"}");
		Assert.assertEquals(TokenType.KEY_WORD, tokens.get(0).getType());
		Assert.assertEquals(TokenType.HIGHEST_PRIORITY_OPERATOR, tokens.get(1).getType());
		Assert.assertEquals(TokenType.CONST, tokens.get(2).getType());
		Assert.assertEquals(TokenType.COMPARISON_OPERATOR, tokens.get(3).getType());
		Assert.assertEquals(TokenType.CONST, tokens.get(4).getType());
		Assert.assertEquals(TokenType.HIGHEST_PRIORITY_OPERATOR, tokens.get(5).getType());
		Assert.assertEquals(TokenType.OPERATOR, tokens.get(6).getType());
		Assert.assertEquals(TokenType.DATA_TYPE, tokens.get(7).getType());
		Assert.assertEquals(TokenType.VAR, tokens.get(8).getType());
		Assert.assertEquals(TokenType.LOW_PRIORITY_OPERATOR, tokens.get(9).getType());
		Assert.assertEquals(TokenType.STRING_CONST, tokens.get(10).getType());
		Assert.assertEquals(TokenType.OPERATOR, tokens.get(11).getType());

	}

	@Test
	public void throwsExceptionWithSpecificType() {
		thrown.expect(CancellationException.class);
		Tokenizer tokenizer = new Tokenizer();
		ArrayList<Token> tokens = tokenizer.tokenizeString("if(1==1){long w=\"siema}");
	}
	
	@Test
	public void instructionTest(){
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("a=a+b*c;");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseStandardInstruction(node);
		
		Node<Token> equals = node.getChildren().get(0);
		Node<Token> ae = equals.getChildren().get(0);
		Node<Token> add = equals.getChildren().get(1);
		Node<Token> a = add.getChildren().get(0);
		Node<Token> mul = add.getChildren().get(1);
		Node<Token> b = mul.getChildren().get(0);
		Node<Token> c = mul.getChildren().get(1);
		Assert.assertEquals("c", c.getData().getValue());
		Assert.assertEquals("b", b.getData().getValue());
		Assert.assertEquals("a", a.getData().getValue());
		Assert.assertEquals("a", ae.getData().getValue());
		
	}
	@Test
	public void declarationWithInitializationTest(){
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("string a=a+b*c;");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseCompoundInstruction(node);
		
		Node<Token> dataType = node.getChildren().get(0);
		Node<Token> name = dataType.getChildren().get(0);
		Node<Token> equals = node.getChildren().get(1);
		Node<Token> ae = equals.getChildren().get(0);
		Node<Token> add = equals.getChildren().get(1);
		Node<Token> a = add.getChildren().get(0);
		Node<Token> mul = add.getChildren().get(1);
		Node<Token> b = mul.getChildren().get(0);
		Node<Token> c = mul.getChildren().get(1);
		Assert.assertEquals("c", c.getData().getValue());
		Assert.assertEquals("b", b.getData().getValue());
		Assert.assertEquals("a", a.getData().getValue());
		Assert.assertEquals("a", ae.getData().getValue());
		Assert.assertEquals("a", name.getData().getValue());
		Assert.assertEquals("string", dataType.getData().getValue());
		
	}
	@Test
	public void instructionTestWithBracket(){
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("a=((((a+(b)))))*c;");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseStandardInstruction(node);
		
		Node<Token> equals = node.getChildren().get(0);
		Node<Token> ae = equals.getChildren().get(0);
		Node<Token> mul = equals.getChildren().get(1);
		Node<Token> add = mul.getChildren().get(0);
		Node<Token> c = mul.getChildren().get(1);
		Node<Token> a = add.getChildren().get(0);
		Node<Token> b = add.getChildren().get(1);
		Assert.assertEquals("c", c.getData().getValue());
		Assert.assertEquals("b", b.getData().getValue());
		Assert.assertEquals("a", a.getData().getValue());
		
	}
	@Test
	public void functionTest(){
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.tokenizeString("c.length(print());");
		Parser parser = new Parser(tokenizer);
		Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
		parser.parseInstruction(node);
		
	}

}
