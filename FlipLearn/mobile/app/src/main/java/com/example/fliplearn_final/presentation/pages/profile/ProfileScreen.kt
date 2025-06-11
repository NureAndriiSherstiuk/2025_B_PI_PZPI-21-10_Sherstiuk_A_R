package com.example.fliplearn_final.presentation.pages.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomButton
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography
import com.example.fliplearn_final.presentation.ui.widgets.ColorPickerCircle
import com.example.fliplearn_final.presentation.ui.widgets.Dropdown


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToStart: () -> Unit,
    ) {
    Scaffold(
        topBar = {},
        content = {
            ProfileScreenContent(modifier = Modifier.padding(it) , viewModel = viewModel , onNavigateToStart = onNavigateToStart)
        },
    )
}



@Composable
fun ProfileScreenContent(modifier: Modifier, viewModel: ProfileViewModel , onNavigateToStart: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadUserProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
        ,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ColorPickerCircle()
                Spacer(modifier = Modifier.height(15.dp))
                CustomText(
                    text = uiState?.username ?:"username",
                    style = LocalAppTypography.current.titleSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        CustomText(
            text = "Особиста інформація",
            style = LocalAppTypography.current.bodyLarge,
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomText(
            text = "Ім'я користувача",
            modifier = Modifier.padding(bottom = 5.dp),
            style = LocalAppTypography.current.displaySmall
        )
        ProfileOutlinedTextField(
            viewModel = viewModel,
            value = uiState.username,
            onValueChange = { viewModel.onEvent(ProfileEvent.UsernameChanged(it)) },
            isIcon = false,
            placeholderText = "Введіть iм'я користувача",
            isPasswordVisible = false
        )

        Spacer(modifier = Modifier.height(15.dp))

        CustomText(
            text = "Електронна пошта",
            modifier = Modifier.padding(bottom = 5.dp),
            style = LocalAppTypography.current.displaySmall
        )
        ProfileOutlinedTextField(
            viewModel = viewModel,
            value = uiState.email,
            onValueChange = { viewModel.onEvent(ProfileEvent.EmailChanged(it)) },
            isIcon = false,
            placeholderText = "Введіть адресу електронної пошти",
            isPasswordVisible = false
        )

        Spacer(modifier = Modifier.height(45.dp))

        CustomText(
            text = "Оформлення",
            style = LocalAppTypography.current.bodyLarge,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomText(
                text = "Тема",
                style = LocalAppTypography.current.labelLarge,
            )
            Dropdown(
                items = uiState.themes.map { it.displayName },
                selectedItem = uiState.selectedTheme.displayName,
                textName = "Тема",
                isRounded = true,
                fieldWidth = 110.dp,
                height = 43.dp,
                onItemSelected = { selected ->
                    val theme = Theme.entries.find { it.displayName == selected }
                    theme?.let { viewModel.onEvent(ProfileEvent.ThemeChanged(it)) }
                }
            )
        }


        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            thickness = 0.8.dp,
            color = LocalAppColors.current.borderColor
        )

        Spacer(modifier = Modifier.height(70.dp))

        CustomButton(text = "Зберегти", onClick = { viewModel.onEvent(ProfileEvent.SaveProfile) })

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedButton(
            onClick = {
                coroutineScope.apply {
                    viewModel.signOutAll(
                        onSuccess = {
                            onNavigateToStart()
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(7.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = LocalAppColors.current.borderColor,
                containerColor =  LocalAppColors.current.primaryBackground,
            )
        ) {
            CustomText(
                text = "Вийти з акаунту",
                style = LocalAppTypography.current.bodySmall
            )
        }

    }
}




@Composable
private fun ProfileOutlinedTextField(
    viewModel: ProfileViewModel,
    value: String,
    onValueChange: (String) -> Unit,
    isIcon: Boolean = true,
    placeholderText: String,
    isPasswordVisible: Boolean
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            CustomText(
                text = placeholderText,
                style = LocalAppTypography.current.labelMedium,
                color = LocalAppColors.current.hintTextColor
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = LocalAppColors.current.surfaceVariant,
            unfocusedTextColor = LocalAppColors.current.headerTextColor,
            unfocusedLabelColor = LocalAppColors.current.hintTextColor,
            unfocusedPlaceholderColor = LocalAppColors.current.hintTextColor,

            focusedBorderColor = LocalAppColors.current.actionTextColor,
            focusedTextColor = LocalAppColors.current.headerTextColor,
        ),
        visualTransformation = if (isIcon && !isPasswordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = if (isIcon)
            KeyboardOptions(keyboardType = KeyboardType.Password)
        else
            KeyboardOptions.Default,
        trailingIcon = {
            if (isIcon) {
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible)
                                R.drawable.ic_visibility
                            else
                                R.drawable.ic_visibility_off
                        ),
                        contentDescription = if (isPasswordVisible)
                            "Hide password"
                        else
                            "Show password"
                    )
                }
            }
        },
        singleLine = true,
    )
}