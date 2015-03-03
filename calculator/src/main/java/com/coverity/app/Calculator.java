

package com.coverity.app;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

public class Calculator {
	
	private static final String ADD = "add";
	private static final String SUB = "sub";
	private static final String MULT = "mult";
	private static final String DIV = "div";
	private static final String LET = "let";
	private static final String illegalArgMsg = "Input argument not formed properly. Correct input format: java Calculator \"add(1, 2)\"";

	final static Logger logger = Logger.getLogger(Calculator.class);
	
	private Map<String, LinkedList<Integer>> varMap;
	
	/*
	 * constructor
	 */
	public Calculator() {
		this.varMap = new HashMap<String, LinkedList<Integer>>();
	}
	
	/*
	 * method checks for matching parans for the given expr
	 *
	 */
	private boolean checkMatchedParans(String expr) {
		try {
			int paranCounter = 0;
			for (int i = 0; i < expr.length(); i++) {
			
				if (expr.charAt(i) == '(') 
					paranCounter++;
			
				if (expr.charAt(i) == ')') {
					if(paranCounter == 0) 
						throw new IllegalArgumentException(illegalArgMsg);
					paranCounter--;
				}
			
			}
		
			return paranCounter == 0;
		} catch (Exception e){
			logger.error(e);
		}
		return false;
	}
	
	
	/*
	 * method checks for matching parans starting at input prefix 
	 * @param expr - input string 
	 * @param prefix
	 * @return index after the paran match 
	 * Eg: add(add(1,2), 3)
	 * prefix: 4
	 * return: 12
	 */
	
	private static int checkMatchedParansAndReturnNextDelim(String expr, int prefix, Character delim) {
		
		if(logger.isDebugEnabled()){
			logger.debug("String expr = " + expr);
			logger.debug("prefix value = " + prefix);
			logger.debug("delimiter = " + delim);
		}
		int i = prefix;
		try {
			int paranCounter = 0;
			for (; i < expr.length(); i++) {
			
				if (paranCounter == 0 && expr.charAt(i) == delim)
					return i;
			
				if (expr.charAt(i) == '(') 
					paranCounter++;
			
				if (expr.charAt(i) == ')') {
					if(paranCounter == 0) 
						throw new IllegalArgumentException(illegalArgMsg);
					paranCounter--;
				}
			
			}
		
			if(paranCounter > 0) 
				throw new IllegalArgumentException(illegalArgMsg);
			
			} catch (Exception e){
				logger.error(e);
			}
			return i;
	}
	
	/*
	 * to check if there is a parenthesis at the beginning
	 */
	private static void checkBeginParen(String expr, int prefix) {
		try {
			if (!expr.startsWith("(", prefix)) {
				throw new IllegalArgumentException(illegalArgMsg);
			}
		} catch (Exception e){
			logger.error(e);
			return;
		}
	}
	
	/*
	 * Syntax check for each of the two expressions for add/sub/mult/div
	 */
	private void syntaxCheckSimpleExprHelper(String expr, String op) {
		checkBeginParen(expr, op.length());
		
		int commaPos = checkMatchedParansAndReturnNextDelim(expr, op.length() + 1, ',');
		String expr1 = expr.substring(op.length() + 1, commaPos);
		syntaxCheckExpr(expr1);
		
		int endPos = checkMatchedParansAndReturnNextDelim(expr, commaPos + 1, ')');
		assert (endPos == expr.length() - 1);
		String expr2 = expr.substring(commaPos + 1, endPos);
		syntaxCheckExpr(expr2);
	}
	
	/*
	 * Syntax check for each of the let expression
	 */
	private void syntaxCheckLetExprHelper(String expr, String op) {
		checkBeginParen(expr, op.length());
		
		int commaPos = checkMatchedParansAndReturnNextDelim(expr, op.length() + 1, ',');
		String varName = expr.substring(op.length() + 1, commaPos);
		if(logger.isDebugEnabled()){
			logger.debug("let label = " + varName);
		}
		syntaxCheckExpr(varName);
		
		
		int secondCommaPos = checkMatchedParansAndReturnNextDelim(expr, commaPos + 1, ',');
		String valueExprName = expr.substring(commaPos + 1, secondCommaPos);
		if(logger.isDebugEnabled()){
			logger.debug("let valueExprName = " + valueExprName);
		}
		syntaxCheckExpr(valueExprName);
		
		
		int endPos = checkMatchedParansAndReturnNextDelim(expr, secondCommaPos + 1, ')');
		String exprName = expr.substring(secondCommaPos + 1, endPos);
		if(logger.isDebugEnabled()){
			logger.debug("let exprName = " + exprName);
		}
		syntaxCheckExpr(exprName);
		
		
	}
	
	/*
	 * check if the expr is a numeric or not
	 */
	private static boolean isNumeric(String expr) {
			String eval = expr;
			if (expr.startsWith("-")) {
				eval = expr.substring(1,expr.length());
			}
	
		 for (Character c: eval.toCharArray()) {
			 	if (!Character.isDigit(c)) {
			 		return false;
			 	}
		 }
			
		return true;
	}
	
	/*
	 * Syntax checking of expressions
	 */
	private void syntaxCheckExpr(String expr) {
		
		try{
		 	if(expr.matches("[a-zA-z]+")) {
		 		
		 	} else if(isNumeric(expr)) {
		 		
		 	} else if (expr.startsWith(ADD)) {
		 		syntaxCheckSimpleExprHelper(expr, ADD);
				
		 	} else if (expr.startsWith(SUB)) {
				syntaxCheckSimpleExprHelper(expr, SUB);
				
		 	} else if (expr.startsWith(MULT)) {
				syntaxCheckSimpleExprHelper(expr, MULT);
				
		 	} else if (expr.startsWith(DIV)) {
				syntaxCheckSimpleExprHelper(expr, DIV);
				
		 	} else if (expr.startsWith(LET)) {
				syntaxCheckLetExprHelper(expr, LET);
				
			} else {
				throw new IllegalArgumentException("unknown operation provided -- need add/sub/mult/div/let");
			}
			
			if (!checkMatchedParans(expr)) 
				throw new IllegalArgumentException("Paranthesis not matching");
			
		} catch (Exception e){
			logger.error(e);
			return;
		}
			
	
			
	}
	
	
	/*
	 * Returns two exprs from the given expr
	 * Eg: In is "add(expr1, expr2)",
	 * out is array of [expr1, expr2]
	 */
	private String[] getTwoExpr(String expr, String op) {
		
		String[] exprArr = new String[2]; 
		
		int commaPos = checkMatchedParansAndReturnNextDelim(expr, op.length() + 1, ',');
		String expr1 = expr.substring(op.length() + 1, commaPos);
		exprArr[0] = expr1;
		
		int endPos = checkMatchedParansAndReturnNextDelim(expr, commaPos + 1, ')');
		String expr2 = expr.substring(commaPos + 1, endPos);
		exprArr[1] = expr2;
		
		return exprArr;
	}
	
	/*
	 * Returns two exprs from the given expr
	 * Eg: In is "let(label, expr1, expr2)",
	 * out is array of [label, expr1, expr2]
	 */
	private String[] get3LetExpr(String expr, String op) {
		String[] exprArr = new String[3]; 
		
		int commaPos = checkMatchedParansAndReturnNextDelim(expr, op.length() + 1, ',');
		String label = expr.substring(op.length() + 1, commaPos);
		exprArr[0] = label;
		if(logger.isDebugEnabled()){
			logger.debug("let label = " + label);
		}
		
		int secondCommaPos = checkMatchedParansAndReturnNextDelim(expr, commaPos + 1, ',');
		String expr1 = expr.substring(commaPos + 1, secondCommaPos);
		exprArr[1] = expr1;
		
		if(logger.isDebugEnabled()){
			logger.debug("let expr1 = " + expr1);
		}
		
		int endPos = checkMatchedParansAndReturnNextDelim(expr, secondCommaPos + 1, ')');
		String expr2 = expr.substring(secondCommaPos + 1, endPos);
		exprArr[2] = expr2;
		if(logger.isDebugEnabled()){
			logger.debug("let expr2 = " + expr2);
		}
		
		return exprArr;
	
	}
	
	/*
	 * Expression evaluator
	 */
	private int exprEval(String expr) {
	
	try {
		if(expr.matches("[a-zA-z]+")) {
			if (varMap.containsKey(expr)) {
				return varMap.get(expr).peek();
			} else {
				throw new IllegalArgumentException("The variable in let is not found");
				}
	 		
	 	} else if(isNumeric(expr)) {
	 		if(logger.isDebugEnabled()){
				logger.debug("Expression is a number: Expr0 = " + Integer.parseInt(expr));
			}
			return Integer.parseInt(expr);
			
		} else if (expr.startsWith(ADD)) {
			
			String[] exprs = getTwoExpr(expr, ADD);
			if(logger.isDebugEnabled()){
				logger.debug("ADD Expression: Expr0 = " + exprs[0] + ", Expr1 = " + exprs[1]);
			}
			return exprEval(exprs[0]) + exprEval(exprs[1]);
			 
		} else if (expr.startsWith(SUB)) {
			
			String[] exprs = getTwoExpr(expr, SUB);
			if(logger.isDebugEnabled()){
				logger.debug("SUB Expression: Expr0 = " + exprs[0] + ", Expr1 = " + exprs[1]);
			}
			return exprEval(exprs[0]) - exprEval(exprs[1]);
		
		} else if(expr.startsWith(MULT)) {
			
			String[] exprs = getTwoExpr(expr, MULT);
			if(logger.isDebugEnabled()){
				logger.debug("MULT Expression: Expr0 = " + exprs[0] + ", Expr1 = " + exprs[1]);
			}
			return exprEval(exprs[0]) * exprEval(exprs[1]);
		
		} else if(expr.startsWith(DIV)) {
			
			String[] exprs = getTwoExpr(expr, DIV);
			if(logger.isDebugEnabled()){
				logger.debug("DIV Expression: Expr0 = " + exprs[0] + ", Expr1 = " + exprs[1]);
			}
			return exprEval(exprs[0]) / exprEval(exprs[1]);
		
		} else if(expr.startsWith(LET)) {
			String[] exprs = get3LetExpr(expr, LET);
			String label = exprs[0];
			String expr1 = exprs[1];
			String expr2 = exprs[2];
			if(logger.isDebugEnabled()){
				logger.debug("Let Expression: Label = " + label + ", Expr1 = " + expr1 + ", Expr2 = " + expr2);
			}
			int valExpr1 = exprEval(expr1);
			LinkedList<Integer> currStack;
			if (!varMap.containsKey(label)) {
				currStack = new LinkedList<Integer>();
				varMap.put(label, currStack);
			}
			varMap.get(label).push(valExpr1);
			
			
			int valExpr2 = exprEval(expr2);
			
			LinkedList<Integer> prevStack = varMap.get(label);
			prevStack.pop();
			if (prevStack.isEmpty()) {
				varMap.remove(label);
			}
			
			return valExpr2;
		
		}else {
			
		} 
		
	} catch (Exception e){
		logger.error(e);
	}
		
		return 0;
	}
	
	public static void main(String[] args) {
		try {
			if (args.length < 1 || args.length > 1) {
				throw new IllegalArgumentException(illegalArgMsg);
			}
		} catch (Exception e){
			logger.error(e);
			return;
		}
		Calculator myCal = new Calculator();
		myCal.syntaxCheckExpr(args[0]);
		System.out.println(myCal.exprEval(args[0]));
		
	}
	
}
