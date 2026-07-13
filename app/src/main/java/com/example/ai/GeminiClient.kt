package com.example.ai

import com.example.BuildConfig
import com.example.data.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.util.Date

object GeminiClient {

    suspend fun parseExpense(prompt: String): Expense? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (hasKey) {
            val systemPrompt = """
                You are an AI expense tracker assistant.
                Extract the expense details from the user's natural language input.
                Respond strictly in valid JSON format with the following keys:
                - "amount": (number) The total amount spent.
                - "category": (string) One of [Food, Travel, Fuel, Shopping, Bills, Education, Medical, Rent, Salary, Investment, Recharge, Subscription, Electronics, Entertainment, Lent, Received, Other].
                - "merchant": (string) The name of the store, person, or biller.
                - "paymentMethod": (string) How it was paid (e.g., UPI, Cash, Card).
                - "notes": (string) Any extra details.
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = prompt)))
                ),
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                generationConfig = GenerationConfig(
                    responseFormat = ResponseFormat(
                        text = ResponseFormatText(
                            mimeType = "application/json",
                            schema = buildJsonObject {
                                put("type", "OBJECT")
                                putJsonObject("properties") {
                                    putJsonObject("amount") { put("type", "NUMBER") }
                                    putJsonObject("category") { put("type", "STRING") }
                                    putJsonObject("merchant") { put("type", "STRING") }
                                    putJsonObject("paymentMethod") { put("type", "STRING") }
                                    putJsonObject("notes") { put("type", "STRING") }
                                }
                            }
                        )
                    ),
                    temperature = 0.1f
                )
            )

            try {
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (jsonText != null) {
                    val json = Json.parseToJsonElement(jsonText).jsonObject
                    val amount = json["amount"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
                    val category = json["category"]?.jsonPrimitive?.content ?: "Other"
                    val merchant = json["merchant"]?.jsonPrimitive?.content ?: ""
                    val paymentMethod = json["paymentMethod"]?.jsonPrimitive?.content ?: "Unknown"
                    val notes = json["notes"]?.jsonPrimitive?.content ?: ""
                    
                    return@withContext Expense(
                        amount = amount,
                        category = category,
                        merchant = merchant,
                        paymentMethod = paymentMethod,
                        notes = notes,
                        date = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Fallback local parsing if API fails or key is missing
        val amount = prompt.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0
        val merchantWords = prompt.split(" ").filter { it.length > 2 && !it.contains(Regex("[0-9]")) }
        val merchant = if (merchantWords.isNotEmpty()) merchantWords.first() else "Unknown"
        
        return@withContext Expense(
            amount = amount,
            category = "Other",
            merchant = merchant,
            paymentMethod = "Unknown",
            notes = prompt,
            date = System.currentTimeMillis()
        )
    }
    
    suspend fun getInsights(expenses: List<Expense>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext "API key missing. Unable to generate insights."
        
        val expenseSummary = expenses.joinToString("\n") { 
            "${Date(it.date)}: ${it.merchant} - ₹${it.amount} (${it.category})" 
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = "Here are my recent expenses:\n$expenseSummary\n\nPlease analyze my spending and give me 2-3 short, natural-language insights or tips. Keep it concise.")))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.7f
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            return@withContext response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No insights available right now."
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Error analyzing expenses."
        }
    }

    suspend fun getShortInsight(expenses: List<Expense>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext "Keep track of your expenses."
        
        val expenseSummary = expenses.take(10).joinToString("\n") { 
            "${Date(it.date)}: ${it.merchant} - ₹${it.amount} (${it.category})" 
        }
        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = "Here are my recent expenses:\n$expenseSummary\n\nGive me one short, 1-sentence insight (under 12 words) about my spending.")))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.5f
            )
        )
        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            return@withContext response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.replace("\"", "") ?: "You're doing great this week."
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "You're doing great this week."
        }
    }

    suspend fun chatWithAI(expenses: List<Expense>, message: String, history: List<Pair<String, String>>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@withContext "API key missing."
        
        val expenseSummary = expenses.take(30).joinToString("\n") { 
            "${Date(it.date)}: ${it.merchant} - ₹${it.amount} (${it.category})" 
        }

        val systemPrompt = """
            You are a helpful financial AI assistant for the Kharcha app. 
            Answer the user's questions about their expenses. Be concise and friendly.
            Here is the user's recent expense data context:
            $expenseSummary
        """.trimIndent()
        
        val allContents = mutableListOf<Content>()
        
        // We simulate chat history with user/model pairs (assuming role isn't explicitly defined in these simple classes, we just concatenate if needed, or if role is needed, we'll just format it as a single prompt for simplicity)
        var fullPrompt = ""
        history.forEach { (role, text) ->
            fullPrompt += "$role: $text\n"
        }
        fullPrompt += "User: $message\nAssistant: "

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = fullPrompt)))
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(
                temperature = 0.7f
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            return@withContext response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I'm not sure how to respond to that."
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Error communicating with AI."
        }
    }
}
