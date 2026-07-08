package com.hritik.recipevault.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.hritik.recipevault.R

@Composable
fun StepDialog(
    initialDescription: String = "",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialDescription.isEmpty()) stringResource(id = R.string.add_step_title)
                else stringResource(id = R.string.edit_step_title)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = { 
                        if (it.length <= 60 || it.length < description.length) {
                            description = it 
                        }
                    },
                    label = { Text(stringResource(id = R.string.step_desc_label)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text(
                            text = stringResource(id = R.string.char_limit_format, description.length, 60),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (description.isNotBlank() && description.length <= 60) {
                        onConfirm(description)
                        onDismiss()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.save_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel_btn))
            }
        }
    )
}
