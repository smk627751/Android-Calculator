package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Stack

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val editText:TextView = findViewById(R.id.input)
        val clear:Button = findViewById(R.id.clear)
        val del:Button = findViewById(R.id.del)
        val equals:Button = findViewById(R.id.equals)
        val buttons = getAllButtons(findViewById(android.R.id.content))
        buttons.forEach{button ->
            button.setOnClickListener{
                editText.text = editText.text.toString() + button.text
            }
        }
        clear.setOnClickListener {
            editText.text = ""
        }
        del.setOnClickListener {
            if(editText.text.isNotEmpty())
            {
                editText.text = editText.text.toString().substring(0,editText.text.length - 1)
            }
        }
        equals.setOnClickListener {
            val postFix = infixToPostfix(editText.text.toString())
            var result = evaluate(postFix)
            if(result.endsWith(".0"))
            {
                result = result.replace(".0","")
            }
            editText.text = result
        }
    }

    private fun getAllButtons(view: ViewGroup): List<Button> {
        val buttons = mutableListOf<Button>()
        for (i in 0 until view.childCount)
        {
            val child = view.getChildAt(i)
            if (child is ViewGroup) {
                buttons.addAll(getAllButtons(child))
            } else if (child is Button) {
                buttons.add(child)
            }
        }
        return buttons
    }

    private fun infixToPostfix(expression: String) : String
    {
        val stack = Stack<Char>()
        val postFix = StringBuilder()
        val arr = expression.toCharArray()
        var i = 0
        while(i < arr.size)
        {
            val ch:Char = arr[i]
            if(Character.isDigit(ch) || ch == '.')
            {
                while(i < arr.size && (Character.isDigit(arr[i]) || arr[i] == '.'))
                {
                    postFix.append(arr[i++])
                }
                i--
                postFix.append(' ')
            }
            else if(ch == '(')
            {
                stack.push(ch)
            }
            else if(ch == ')')
            {
                while (stack.isNotEmpty() && stack.peek() != '(')
                {
                    postFix.append(stack.pop()).append(' ')
                }
                if(stack.isNotEmpty())
                {
                    stack.pop()
                }
            }
            else
            {
                while (stack.isNotEmpty() && getPrecedence(ch) <= getPrecedence(stack.peek()))
                {
                    postFix.append(stack.pop()).append(' ')
                }
                stack.push(ch)
            }
            i++
        }
        while (stack.isNotEmpty())
        {
            postFix.append(stack.pop()).append(' ')
        }
        return postFix.toString().trim()
    }

    private fun getPrecedence(ch: Char): Int {
        return when(ch)
        {
            '+','-' -> 1
            '*','/','%' -> 2
            else -> 0
        }
    }

    private fun evaluate(expression: String) : String
    {
        val stack = Stack<Double>()
        val operators = "+-*/"
        val tokens = expression.split(" ")
        for(token in tokens)
        {
            if(operators.contains(token))
            {
                val right = when(stack.isNotEmpty()) {
                    true -> stack.pop()
                    else -> 0.0
                }
                val left = when(stack.isNotEmpty()){
                    true -> stack.pop()
                    else -> 0.0
                }
                when(token)
                {
                    "+" -> stack.push(left + right)
                    "-" -> stack.push(left - right)
                    "*" -> stack.push(left * right)
                    "/" -> stack.push(left / right)
                }
            }
            else
            {
                try {
                    stack.push(token.toDouble())
                }catch (e : Exception)
                {
                    
                }
            }
        }
        return when(stack.isNotEmpty()) {
            true -> stack.pop().toString()
            else -> ""
        }
    }
}