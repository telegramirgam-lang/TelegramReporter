package com.example.telegramreporter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val emailList = listOf(
        "client.care@cibc.com",
        "fraud@cibc.com",
        "Mailbox.InvestorRelations@cibc.com",
        "cibcpwm.privacy@cibc.com",
        "corporate.secretary@cibc.com",
        "tom.wallis@cibc.com",
        "mailbox.clientcomplaintappeals@cibc.com",
        "deborah.rowe@cibc.com",
        "rob.weiss@cibc.com"
    )

    private val emailSubject =
        "Report of Telegram Accounts Impersonating Bank CIBC"

    private val emailMessage = """
Dear Bank CIBC Team,

I would like to report multiple Telegram accounts impersonating Bank CIBC. These accounts appear to misuse his identity and may mislead users by appearing to represent the company.

Please review the reported accounts and, if confirmed as unauthorized, contact Telegram to have them marked as Fake to help protect your brand and users.

Reported accounts:

""".trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TelegramReporterScreen(
                        onNext = { links ->
                            openEmailApp(links)
                        }
                    )
                }
            }
        }
    }

    private fun openEmailApp(links: List<String>) {

        val cleanLinks = links
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (cleanLinks.isEmpty()) {
            return
        }

        val linksText = cleanLinks.joinToString("\n")

        val finalMessage = emailMessage + linksText

        val recipients = emailList
            .distinct()
            .joinToString(",")

        val emailUri = Uri.Builder()
            .scheme("mailto")
            .path(recipients)
            .appendQueryParameter(
                "subject",
                emailSubject
            )
            .appendQueryParameter(
                "body",
                finalMessage
            )
            .build()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = emailUri
        }

        startActivity(
            Intent.createChooser(
                intent,
                "Choose Email App"
            )
        )
    }
}

@Composable
fun TelegramReporterScreen(
    onNext: (List<String>) -> Unit
) {

    var links by remember {
        mutableStateOf(listOf(""))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Telegram Reporter",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Add Telegram account links",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            itemsIndexed(links) { index, link ->

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = link,
                        onValueChange = { newValue ->

                            val updatedLinks =
                                links.toMutableList()

                            updatedLinks[index] = newValue

                            links = updatedLinks
                        },
                        label = {
                            Text(
                                "Telegram Link ${index + 1}"
                            )
                        },
                        placeholder = {
                            Text(
                                "https://t.me/example"
                            )
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    if (links.size > 1) {

                        IconButton(
                            onClick = {

                                val updatedLinks =
                                    links.toMutableList()

                                updatedLinks.removeAt(index)

                                links = updatedLinks
                            }
                        ) {
                            Text("✕")
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            onClick = {
                links = links + ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Add Telegram Link")
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            onClick = {
                onNext(links)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next →")
        }
    }
}
