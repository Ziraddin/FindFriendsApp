package com.example.findfriendsapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onClickNext: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GreetingText(
            name = name,
            modifier
                .padding(bottom = 16.dp)
                .animateContentSize()
        )
        SetUsername(name = name, onNameChange = onNameChange)
        NextButton(
            name = name, modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            onClickNext(name)
        }
    }
}

@Composable
fun GreetingText(name: String, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = name.isNotEmpty()
    ) {
        Text(text = "Hello, $name!", modifier = modifier)
    }
}

@Composable
fun NextButton(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier, horizontalArrangement = Arrangement.End
    ) {
        AnimatedVisibility(
            visible = name.isNotEmpty(),
            enter = slideInFromLeftWithDelay(duration = 300, delay = 300),
            exit = slideOutToRightWithDelay(duration = 300)
        ) {
            ElevatedButton(onClick = onClick, modifier = Modifier) {
                Text(text = "Next", modifier = Modifier.padding(end = 4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward, contentDescription = "continue button"
                )
            }
        }
    }
}

@Composable
fun SetUsername(modifier: Modifier = Modifier, name: String, onNameChange: (String) -> Unit) {
    OutlinedTextField(modifier = modifier,
        value = name,
        maxLines = 1,
        onValueChange = { newName -> onNameChange(newName) },
        label = { Text(text = "Name") })
}