package tokenizerTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import Tree.Node;
import Tree.Tree;
import files_loader.EndOfFileException;
import files_loader.SourceLoader;
import parser.Parser;
import parser.Statement;
import parser.statements.FunctionStatement;
import parser.statements.IfStatement;
import parser.statements.VarDeclaration;
import parser.values.Equal;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;
import tokenizer.VarToken;

public class TokenizerTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void emptyFunctionParse() throws IOException {
		Tokenizer tokenizer = new Tokenizer(new SourceLoader(
				// @formatter:off
				 "func alfa(long a, long z){	"
				 + "string zmienna=\"aaa\";	"
				 + "if(zmienna==\"siema\"){ 		"
					 + "for(long i=0;i<zmienna.length();++i){		"
					 + "zmienna+=\"a\"; 		"
					 + "};		"
					 + "string f;		"
					 + "string a;		"
					 + "string b;	"
					 + "string c;		"
					 + "string sok;		"
					 + "while(((f|a))&b& c!=\"sok\"){			"
					 	+ "c-=sok;			"
					 + "};"
					 + "c.length(print());"
				 + "};"
				+ "}"));
		// @formatter:on
		Parser parser = new Parser(tokenizer);
		parser.parse();
		HashMap<String, FunctionStatement> functions = parser.getFunctions();
		FunctionStatement function = functions.get("alfa");
		Assert.assertEquals(2, function.getArguments().size());
		Assert.assertEquals(FunctionStatement.class, function.getClass());
		ArrayList<Statement> instructions = function.getInstructions();
		Assert.assertEquals(VarDeclaration.class,instructions.get(0).getClass());
		Assert.assertEquals(Equal.class,instructions.get(1).getClass());
		Assert.assertEquals(IfStatement.class,instructions.get(2).getClass());
		Assert.assertEquals(3,instructions.size());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(4).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(5).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(6).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(7).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(8).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(9).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(10).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(11).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(12).getClass());
//		Assert.assertEquals(VarDeclaration.class,instructions.get(13).getClass());
	}

	// @Test
	// public void parseSimpleIf() {
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("if(a){})");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseIf(node);
	// Node<Token> ifek = node.getChildren().get(0);
	// Node<Token> cond = ifek.getChildren().get(0);
	// Node<Token> a = cond.getChildren().get(0);
	//
	//
	// Assert.assertEquals("a", a.getData().getValue());
	//
	//
	// }
	// @Test
	// public void parseIf() {
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("if(a & b | c){})");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseIf(node);
	// Node<Token> ifek = node.getChildren().get(0);
	// Node<Token> cond = ifek.getChildren().get(0);
	// Node<Token> or = cond.getChildren().get(0);
	// Node<Token> and = or.getChildren().get(0);
	// Node<Token> a = and.getChildren().get(0);
	// Node<Token> b = and.getChildren().get(1);
	// Node<Token> c = or.getChildren().get(1);
	//
	//
	// Assert.assertEquals("c", c.getData().getValue());
	// Assert.assertEquals("b", b.getData().getValue());
	// Assert.assertEquals("a", a.getData().getValue());
	//
	//
	// }
	// @Test
	// public void parseIfWithDoubleBracket() {
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("if(a & (b & (c | d | e))){})");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseIf(node);
	// Node<Token> ifek = node.getChildren().get(0);
	// Node<Token> cond = ifek.getChildren().get(0);
	// Node<Token> and = cond.getChildren().get(0);
	// Node<Token> a = and.getChildren().get(0);
	// Node<Token> andBr = and.getChildren().get(1);
	// Node<Token> b = andBr.getChildren().get(0);
	// Node<Token> orBrd = andBr.getChildren().get(1);
	// Node<Token> c = orBrd.getChildren().get(0);
	// Node<Token> d = orBrd.getChildren().get(1);
	// Node<Token> e = orBrd.getChildren().get(2);
	//
	//
	// Assert.assertEquals("c", c.getData().getValue());
	// Assert.assertEquals("b", b.getData().getValue());
	// Assert.assertEquals("a", a.getData().getValue());
	// Assert.assertEquals("d", d.getData().getValue());
	// Assert.assertEquals("e", e.getData().getValue());
	// }
	// @Test
	// public void parseIfWithBracket() {
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("if(a & (b | c)){};");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseIf(node);
	// Node<Token> ifek = node.getChildren().get(0);
	// Node<Token> cond = ifek.getChildren().get(0);
	// Node<Token> and = cond.getChildren().get(0);
	// Node<Token> a = and.getChildren().get(0);
	// Node<Token> orBr = and.getChildren().get(1);
	// Node<Token> b = orBr.getChildren().get(0);
	// Node<Token> c = orBr.getChildren().get(1);
	//
	// Assert.assertEquals("c", c.getData().getValue());
	// Assert.assertEquals("b", b.getData().getValue());
	// Assert.assertEquals("a", a.getData().getValue());
	// }

	@Test
	public void tokenizeTest() {
		try {
			Tokenizer tokenizer = new Tokenizer(new SourceLoader("if(1==1){long w=\"siema\"}"));
			Token token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.IF, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.L_BRACKET, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.CONST, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.EQUALS, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.CONST, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.R_BRACKET, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.L_BR, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.LONG, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.VAR, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.EQUAL, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.STRING_CONST, token.getType());
			token = tokenizer.tokenizeOneToken();
			Assert.assertEquals(TokenType.R_BR, token.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void throwsExceptionWithSpecificType() {
		try {
			thrown.expect(EndOfFileException.class);
			Tokenizer tokenizer = new Tokenizer(new SourceLoader("if(1==1){long w=\"siema}"));
			for (int i = 0; i < 11; ++i)
				tokenizer.tokenizeOneToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//
	// @Test
	// public void instructionTest(){
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("a=a+b*c;");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseStandardInstruction(node);
	//
	// Node<Token> equals = node.getChildren().get(0);
	// Node<Token> ae = equals.getChildren().get(0);
	// Node<Token> add = equals.getChildren().get(1);
	// Node<Token> a = add.getChildren().get(0);
	// Node<Token> mul = add.getChildren().get(1);
	// Node<Token> b = mul.getChildren().get(0);
	// Node<Token> c = mul.getChildren().get(1);
	// Assert.assertEquals("c", c.getData().getValue());
	// Assert.assertEquals("b", b.getData().getValue());
	// Assert.assertEquals("a", a.getData().getValue());
	// Assert.assertEquals("a", ae.getData().getValue());
	//
	// }
	// @Test
	// public void declarationWithInitializationTest(){
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("string a=a+b*c;");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseCompoundInstruction(node);
	//
	// Node<Token> dataType = node.getChildren().get(0);
	// Node<Token> name = dataType.getChildren().get(0);
	// Node<Token> equals = node.getChildren().get(1);
	// Node<Token> ae = equals.getChildren().get(0);
	// Node<Token> add = equals.getChildren().get(1);
	// Node<Token> a = add.getChildren().get(0);
	// Node<Token> mul = add.getChildren().get(1);
	// Node<Token> b = mul.getChildren().get(0);
	// Node<Token> c = mul.getChildren().get(1);
	// Assert.assertEquals("c", ((VarToken)c.getData()).getValue());
	// Assert.assertEquals("b",((VarToken) b.getData()).getValue());
	// Assert.assertEquals("a", ((VarToken)a.getData()).getValue());
	// Assert.assertEquals("a",((VarToken) ae.getData()).getValue());
	// Assert.assertEquals("a", ((VarToken)name.getData()).getValue());
	// Assert.assertEquals("string", dataType.getData().getValue());
	//
	// }
	// @Test
	// public void instructionTestWithBracket(){
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("a=((((a+(b)))))*c;");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseStandardInstruction(node);
	//
	// Node<Token> equals = node.getChildren().get(0);
	// Node<Token> ae = equals.getChildren().get(0);
	// Node<Token> mul = equals.getChildren().get(1);
	// Node<Token> add = mul.getChildren().get(0);
	// Node<Token> c = mul.getChildren().get(1);
	// Node<Token> a = add.getChildren().get(0);
	// Node<Token> b = add.getChildren().get(1);
	// Assert.assertEquals("c", c.getData().getValue());
	// Assert.assertEquals("b", b.getData().getValue());
	// Assert.assertEquals("a", a.getData().getValue());
	// Assert.assertEquals("a", ae.getData().getValue());
	//
	// }
	// @Test
	// public void functionTest(){
	// Tokenizer tokenizer = new Tokenizer();
	// tokenizer.tokenizeString("c.length(print());");
	// Parser parser = new Parser(tokenizer);
	// Node<Token> node = new Node<Token>(new Token("", TokenType.KEY_WORD,0));
	// parser.parseInstruction(node);
	//
	// }

}
