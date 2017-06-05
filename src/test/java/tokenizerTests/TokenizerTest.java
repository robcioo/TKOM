package tokenizerTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import app.Wrapper;
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

public class TokenizerTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void SortTest() throws IOException {
		Tokenizer tokenizer = new Tokenizer(new SourceLoader(
				// @formatter:off
				"func sortArray(list x) {"
				+ "list l={};"
				+ "while(x.length()>0){"
					+ "long max=x[0];"
					+ "for(long j=0;j<x.length();++j){"
						+ "if(x[j]>max){"
							+ "max=x[j];"
						+ "};"
					+ "};"
					+ "l+=max;"
					+ "x-=max;"
					+ "max=-1;"
				+ "};"
				+ "return l;"
			+ "}"));
		// @formatter:on
		Parser parser = new Parser(tokenizer);
		parser.parse();
		Assert.assertEquals(new ArrayList<>(Arrays.asList(new Long(5),new Long(4),new Long(3),new Long(2),new Long(1))), parser.execute("sortArray",
				new ArrayList<>(Arrays.asList(new Wrapper(new ArrayList<>(
						Arrays.asList(new Long(2),new Long(1),new Long(4),new Long(3),new Long(5),new Long(5))
						))))));
	}

	@Test
	public void RecursionTest() throws IOException {
		Tokenizer tokenizer = new Tokenizer(new SourceLoader(
				// @formatter:off
				 "func silnia(long num){"
				 + "if(num>1){"
				 	+ "return num*silnia(num+(-1));};"
				 + "return 1;}"));
				// @formatter:on
		Parser parser = new Parser(tokenizer);
		parser.parse();
		Assert.assertEquals(new Long(120),
				parser.execute("silnia", new ArrayList<>(Arrays.asList(new Wrapper(new Long(5))))));
	}

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
		Assert.assertEquals(VarDeclaration.class, instructions.get(0).getClass());
		Assert.assertEquals(Equal.class, instructions.get(1).getClass());
		Assert.assertEquals(IfStatement.class, instructions.get(2).getClass());
		Assert.assertEquals(3, instructions.size());
	}

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

}
