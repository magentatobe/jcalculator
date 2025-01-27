package org.kmfahey.jcalculator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;


/**
 * Implements the ActionListener interface; used by JButtons defined in
 * JCalculator to handle click events. Stores a reference to the calculator's
 * JTextPane display field. The actionPerformed method handles each button press
 * and updates the display field accordingly.
 */
public class InputHandler implements ActionListener {

    private final JTextPane calculatorField;

    /**
     * Constructs the object, storing the calculator's JTextPane for later.
     *
     * @param calcField The calculator's JTextPane object representing its
     *                  display. */
    public InputHandler(final JTextPane calcField) {
        calculatorField = calcField;
    }

    /**
     * Called by any of the JButtons defined in JCalculator when one is pressed.
     * Extracts the button text from the event and the display text from the
     * calculator's JTextPane, and uses a private handler method based on the
     * button text to determine how to modify the display text. The calculator's
     * JTextPane's text is modified based on the handler method's results.
     *
     * @param event an ActionEvent object generated by the JButton which calls
     *              this method when clicked
     */
    public void actionPerformed(final ActionEvent event) {
        JButton sourceButton = (JButton) event.getSource();
        String displayText = calculatorField.getText();
        if (displayText.equals("Error")) {
            displayText = "0";
        }
        String buttonText = sourceButton.getText();
        String allButLastChar = displayText.substring(0, displayText.length() - 1);
        String newText = displayText;
        switch (buttonText) {
            case "0": case "1": case "2": case "3": case "4": case "5": case "6": case "7": case "8": case "9":
                newText = handleDigit(displayText, buttonText, allButLastChar); break;
            case ".":
                newText = handleDecimalPoint(displayText, buttonText); break;
            case "+": case "-": case "×": case "÷":
                newText = handlePlusMinusMultDiv(displayText, buttonText, allButLastChar); break;
            case "𝘹ʸ":
                newText = handleExponentiation(displayText); break;
            case "√𝘹":
                newText = handleSquareRoot(displayText); break;
            case "(":
                newText = handleLeftParen(displayText, buttonText); break;
            case ")":
                newText = handleRightParen(displayText, buttonText); break;
            case "C":
                newText = handleClear(displayText); break;
            case "⌫":
                newText = handleBackspace(displayText); break;
            case "±":
                newText = handlePlusOrMinus(displayText); break;
            case "1/𝘹":
                newText = handleReciprocal(displayText); break;
            case "=":
                newText = handleEquals(displayText); break;
            default:
                break;
        }
        calculatorField.setText(newText);
    }

    /* Handles one of the digit buttons being pressed. If the display text
     * equals "0", it's replaced with the digit.
     *
     * Otherwise if it's one character long, the digit is appended to the
     * display text.
     *
     * Otherwise if the display text ends with [^0-9.] followed by a "0", the
     * "0" is replaced with the digit.
     *
     * Otherwise the digit is appended to the display text. */
    private String handleDigit(final String displayText, final String buttonText, final String allButLastChar) {
        if (displayText.equals("0")) {
            return buttonText;
        } else if (displayText.length() == 1) {
            return displayText + buttonText;
        } else if (displayText.matches("^.*[^0-9.]0$")) {
            return allButLastChar + buttonText;
        } else {
            return displayText + buttonText;
        }
    }

    /* Handles the "." button being pressed. If the display text is "0", or it's
     * entirely digits, or it ends with [^0-9.] followed by [0-9],
     * a "." is appended to the display text. Otherwise, the display text is
     * unaltered. */
    private String handleDecimalPoint(final String displayText, final String buttonText) {
        if (displayText.equals("0") || displayText.matches("^[0-9]+$") || displayText.matches("^.*[^0-9.][0-9]+$")) {
            return displayText + buttonText;
        } else {
            return displayText;
        }
    }

    /* Handles the "+", "×", "÷", or "-" buttons being pressed. If the display
     * text ends in one of [+×÷^√-], it's replaced by the button text.
     * Otherwise the button text is appended to the end of the display text. */
    private String handlePlusMinusMultDiv(final String displayText, final String buttonText,
                                          final String allButLastChar) {
        if (displayText.matches("^.*[+×÷^√-]$")) {
            return allButLastChar + buttonText;
        } else {
            return displayText + buttonText;
        }
    }

    /* Handles the "𝘹ʸ" button being pressed. If the display text ends in
     * one of [+-×÷^√], it's replaced by a "^". Otherwise, a "^" is appended
     * to the display text. */
    private String handleExponentiation(final String displayText) {
        if (displayText.matches("^.*[+-×÷^√]$")) {
            return displayText.substring(0, displayText.length() - 1) + "^";
        } else {
            return displayText + "^";
        }
    }

    /* Handles the "√" button being pressed. If the display text doesn't
     * end in a digit or a "√", adds a "√" to the end. Otherwise leaves it
     * unchanged. */
    private String handleSquareRoot(final String displayText) {
        if (!displayText.matches("^.*[0-9.√]$")) {
            return displayText + "√";
        } else {
            return displayText;
        }
    }

    /* Handles the "(" button being pressed. If the display text is "0", sets it
     * to "(". If display text doesn't end in a number, adds a left paren at the
     * end. Otherwise, leaves the display text unchanged. */
    private String handleLeftParen(final String displayText, final String buttonText) {
        if (displayText.equals("0")) {
            return buttonText;
        } else if (!displayText.matches("^.*[0-9.]$")) {
            return displayText + buttonText;
        } else {
            return displayText;
        }
    }

    /* Handles the ")" button being pressed. Counts the parentheses in the
     * display string; if the left parens outnumber the right parens, adds a
     * right paren at the end of the display text. Otherwise, leaves the display
     * text unchanged. */
    private String handleRightParen(final String displayText, final String buttonText) {
        int leftParenCount = 0;
        int rightParenCount = 0;
        for (int index = 0; index < displayText.length(); index++) {
            char charAtIndex = displayText.charAt(index);
            if (charAtIndex == '(') {
                leftParenCount++;
            } else if (charAtIndex == ')') {
                rightParenCount++;
            }
        }
        if (!displayText.matches("^.*[+-×÷^√]$") && leftParenCount > rightParenCount) {
            return displayText + buttonText;
        } else {
            return displayText;
        }
    }

    /* Handles the clear button being pressed. Sets the display text to "0". */
    private String handleClear(final String displayText) {
        return "0";
    }

    /* Handles the backspace button being pressed. If the display text is longer
     * than 1 character, the last character is removed; otherwise the display
     * text is set to 0. */
    private String handleBackspace(final String displayText) {
        if (displayText.length() > 1) {
            return displayText.substring(0, displayText.length() - 1);
        } else {
            return "0";
        }
    }

    /* Handles the "±" button being pressed. If the display text already begins
     * with a "-", it's removed; otherwise a "-" is prepended to the display
     * text. */
    private String handlePlusOrMinus(final String displayText) {
        if (displayText.matches("^-.*$")) {
            return displayText.substring(1);
        } else {
            return "-" + displayText;
        }
    }

    /* Handles the "1/x" button being pressed. If the display text is already
     * in a reciprocal, it's undone, otherwise the display text is wrapped in a
     * reciprocal. */
    private String handleReciprocal(final String displayText) {
        if (displayText.matches("^1÷(.*)$")) {
            return displayText.substring(2);
        } else {
            return "1÷(" + displayText + ")";
        }
    }

    /* Handles the "=" button being pressed. Uses ArithmeticParser to build a
     * parse tree of ParseTreeNode objects from the expression in the display,
     * then uses ParseTreeNode.evaluate() to evaluate it to a float result.
     * Returns the result as a String. */
    private String handleEquals(final String displayText) {
        try {
            float expressionValue = ArithmeticParser.parseExpression(displayText).evaluate();
            if (Math.floor(expressionValue) == expressionValue) {
                return String.valueOf((int) expressionValue);
            } else {
                return String.valueOf(expressionValue);
            }
        } catch (IllegalStateException exception) {
            return "Error";
        } catch (ParseException | NullPointerException exception) {
            return displayText;
        }
    }
}
