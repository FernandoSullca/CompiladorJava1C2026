package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = "="
MoreThan = ">"
MoreEqualThan = ">="
LessThan = "<"
LessEqualThan = "<="
Equal = "=="
NotEqual = "!="
OpenBracket = "("
CloseBracket = ")"
OpenKey = "{"
CloseKey = "}"
Letter = [a-zA-Z]
Digit = [0-9]
Colon =":"
Semi = ";"
Inc = "++"
Dec = "--"
Comma =","
OpenArray = "["
CloseArray = "]"

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Digit}+
StringConstant = "\"" ([^\"])* "\""

//comments de una linea o ningun caracter, no necesita cup(gramatica)
CommentCharacters = ([^\r\n])*
Comment = "#+" {CommentCharacters} "+#"


/*  reserve */
Print = "print"
Read = "read"
While = "while"
Init = "init"
If = "if"
Else = "else"
String= "String"
Float= "Float"
Int= "Int"
In = "in"
For = "for"
And = "AND"
Or = "OR"
Not = "NOT"
%%


/* keywords */

<YYINITIAL> {
  /*  reserve */
  {Print}                                   { return symbol(ParserSym.PRINT); }
  {Read}                                    { return symbol(ParserSym.READ); }
  {While}                                   { return symbol(ParserSym.WHILE); }
  {Init}                                    { return symbol(ParserSym.INIT); }
  {If}                                      { return symbol(ParserSym.IF); }
  {Else}                                    { return symbol(ParserSym.ELSE); }
  {String}                                  { return symbol(ParserSym.STRING); }
  {Float}                                   { return symbol(ParserSym.FLOAT); }
  {Int}                                     { return symbol(ParserSym.INT); }
  {In}                                      { return symbol(ParserSym.IN); }
  {For}                                     { return symbol(ParserSym.FOR); }
  {And}                                     { return symbol(ParserSym.AND); }
  {Or}                                      { return symbol(ParserSym.OR); }
  {Not}                                     { return symbol(ParserSym.NOT); }

  /* identifiers */
  {Identifier}                             { return symbol(ParserSym.IDENTIFIER, yytext()); }
  /* Constants */
  {IntegerConstant}                        { return symbol(ParserSym.INTEGER_CONSTANT, yytext()); }

  /* operators */
  {Plus}                                    { return symbol(ParserSym.PLUS); }
  {Sub}                                     { return symbol(ParserSym.SUB); }
  {Mult}                                    { return symbol(ParserSym.MULT); }
  {Div}                                     { return symbol(ParserSym.DIV); }
  {Assig}                                   { return symbol(ParserSym.ASSIG); }
  {MoreThan}                                { return symbol(ParserSym.MORETHAN); }
  {MoreEqualThan}                           { return symbol(ParserSym.MOREEQUALTHAN); }
  {LessThan}                                { return symbol(ParserSym.LESSTHAN); }
  {LessEqualThan}                           { return symbol(ParserSym.LESSEQUALTHAN); }
  {Equal}                                   { return symbol(ParserSym.EQUAL); }
  {NotEqual}                                { return symbol(ParserSym.NOTEQUAL); }
  {OpenBracket}                             { return symbol(ParserSym.OPEN_BRACKET); }
  {CloseBracket}                            { return symbol(ParserSym.CLOSE_BRACKET); }
  {OpenKey}                                 { return symbol(ParserSym.OPEN_KEY); }
  {CloseKey}                                { return symbol(ParserSym.CLOSE_KEY); }
  {Colon}                                   { return symbol(ParserSym.COLON); }
  {Comma}                                   { return symbol(ParserSym.COMMA); }  
  {OpenArray}                               { return symbol(ParserSym.OPEN_ARRAY); }
  {CloseArray}                              { return symbol(ParserSym.CLOSE_ARRAY); }
  {Semi}                                    { return symbol(ParserSym.SEMI); }
  {Inc}                                     { return symbol(ParserSym.INC); }
  {Dec}                                     { return symbol(ParserSym.DEC); }
  
  /* whitespace ,Comment*/
  {WhiteSpace}                   { /* ignore */ }

  {Comment}                      {
          String value = yytext().substring(1, yytext().length() - 1);;
          if (value.indexOf('#') != -1) { //Verifico que termine con el caracter correcto
              throw new UnknownCharacterException(yytext());
          }
      }

  /*constants*/
  {StringConstant}                          {
        String value = yytext().substring(1, yytext().length() - 1);
        if (value.length() > MAX_LENGTH) {
            throw new InvalidLengthException("La longitud del STRING "+value +" supera lo permitido.");
        }
        return symbol(ParserSym.STRING_CONSTANT, value);
  }
}

/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }

