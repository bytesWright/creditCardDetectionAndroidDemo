package com.isdavid.credit_card_detection.compose


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.isdavid.credit_card_detection.view_model.delegates.FieldsVMDC


@Composable
fun CreditCardForm(
    modifier: Modifier = Modifier,
    capture: () -> Unit = {},
    viewModel: FieldsVMDC
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CreditCardImageField(
                imageBitmap = viewModel.frontImage.value, modifier = Modifier.weight(1f)
            )
            CreditCardImageField(
                imageBitmap = viewModel.backImage.value, modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.cardholderName.value,
            onValueChange = { viewModel.setCreditCardNumber(it) },
            label = { Text("Cardholder Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.creditCardNumber.value,
            onValueChange = viewModel::setCreditCardNumber,
            label = { Text("Card Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.date.value,
            onValueChange = viewModel::setCreditCardNumber,
            label = { Text("Expiration Date (MM/YY)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.securityNumber.value,
            onValueChange = viewModel::setCreditCardNumber,
            label = { Text("CVV") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = { capture() }) {
                Text("Capture")
            }

            Button(onClick = {}, modifier = Modifier.weight(1f)) {
                Text("Submit")
            }
        }
    }
}


