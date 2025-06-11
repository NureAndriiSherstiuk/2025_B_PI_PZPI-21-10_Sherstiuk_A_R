package com.example.fliplearn_final.presentation.pages.sign_up

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fliplearn_final.R
import com.example.fliplearn_final.presentation.ui.components.CustomButton
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToStart: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    Scaffold(
        topBar = { TopBar(onNavigateToStart = onNavigateToStart) },
        content = {
            SignUpScreenContent(
                modifier = Modifier.padding(it),
                viewModel = viewModel,
                onNavigateToSignIn = onNavigateToSignIn,
                onNavigateToMain = onNavigateToMain
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onNavigateToStart: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomText(
                    text = "FlipLearn",
                    style = LocalAppTypography.current.titleLarge,
                    color = LocalAppColors.current.headerTextColor
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateToStart,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LocalAppColors.current.primaryBackground,
            titleContentColor = LocalAppColors.current.primaryBackground
        )
    )
}


@Composable
private fun SignUpScreenContent(
    modifier: Modifier,
    viewModel: SignUpViewModel,
    onNavigateToSignIn: () -> Unit,
    onNavigateToMain:() -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val errorMessage = uiState.errorMessage

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        GoogleButton(
            onClick = {
                coroutineScope.launch {
                    viewModel.signUpWithGoogle(
                        onSuccess = {
                            onNavigateToMain()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))

            CustomText(
                text = "або",
                color = LocalAppColors.current.primaryTextColor,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = LocalAppTypography.current.labelSmall
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        CustomText(
            text = "Електронна пошта",
            modifier = Modifier.padding(bottom = 8.dp),
            style = LocalAppTypography.current.labelLarge,
        )

        SignUpOutlinedTextField(
            modifier = Modifier,
            viewModel = viewModel,
            value = uiState.email,
            isIcon = false,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholderText = "Введіть адресу електронної пошти",
            isPasswordVisible = true
        )
        uiState.fieldErrors["email"]?.let { error ->
            CustomText(text = error, color = LocalAppColors.current.notificationColor, style = LocalAppTypography.current.labelSmall)
        }

        Spacer(modifier = Modifier.height(5.dp))

        CustomText(
            text = "Ім'я користувача",
            modifier = Modifier.padding(bottom = 8.dp),
            style = LocalAppTypography.current.labelLarge,
        )

        SignUpOutlinedTextField(
            modifier = Modifier,
            viewModel = viewModel,
            value = uiState.userName,
            isIcon = false,
            onValueChange = { viewModel.onUserNameChange(it) },
            placeholderText = "Введіть ім'я користувача",
            isPasswordVisible = true
        )
        uiState.fieldErrors["userName"]?.let { error ->
            CustomText(text = error, color = LocalAppColors.current.notificationColor, style = LocalAppTypography.current.labelSmall)
        }

        CustomText(
            text = "Пароль",
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            style = LocalAppTypography.current.labelLarge
        )
        SignUpOutlinedTextField(
            modifier = Modifier.padding(bottom = 10.dp),
            viewModel = viewModel,
            value = uiState.password,
            isIcon = false,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholderText = "Введіть пароль",
            isPasswordVisible =  false
        )
        uiState.fieldErrors["password"]?.let { error ->
            CustomText(text = error, color = LocalAppColors.current.notificationColor, style = LocalAppTypography.current.labelSmall)
        }

        SignUpOutlinedTextField(
            modifier = Modifier,
            viewModel = viewModel,
            value = uiState.confirmPassword,
            isIcon = true,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            placeholderText = "Повторіть пароль",
            isPasswordVisible = uiState.isPasswordVisible
        )
        uiState.fieldErrors["confirmPassword"]?.let { error ->
            CustomText(text = error, color = LocalAppColors.current.notificationColor, style = LocalAppTypography.current.labelSmall)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row( modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center) {
            uiState.errorMessage?.let { errorMessage ->
                CustomText(
                    text = errorMessage,
                    color = LocalAppColors.current.notificationColor,
                    style = LocalAppTypography.current.labelSmall
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CustomButton(
                text = if (uiState.isLoading) "Завантаження..." else "Зареєструватися",
                onClick = {
                    viewModel.signUp(
                        onSuccess = {
                            onNavigateToMain()
                        },
                        onError = {}
                    )
                }
            )

            CustomText(
                text = "Вже маєте акаунт FlipLearn?",
                modifier = Modifier.padding(top = 16.dp),
                style = LocalAppTypography.current.labelMedium
            )

            CustomText(
                text = "Увійдіть у профіль.",
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable {
                        onNavigateToSignIn()
                    },
                style = LocalAppTypography.current.labelMedium,
                color = LocalAppColors.current.actionTextColor
            )
        }
        Spacer(modifier = Modifier.weight(.2f))
    }
}

@Composable
private fun GoogleButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick =  onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = LocalAppColors.current.headerTextColor
        ),
        shape = RoundedCornerShape(7.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google Icon",
            modifier = Modifier.size(18.dp)
        )
        CustomText(
            text = "Зареєструватися через Google",
            modifier = Modifier.padding(start = 8.dp),
            style = LocalAppTypography.current.labelMedium,
            color = LocalAppColors.current.headerTextColor
        )
    }
}

@Composable
private fun SignUpOutlinedTextField(
    modifier: Modifier,
    viewModel: SignUpViewModel,
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
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = LocalAppColors.current.surfaceVariant,
            unfocusedTextColor = LocalAppColors.current.headerTextColor,
            unfocusedLabelColor = LocalAppColors.current.hintTextColor,
            unfocusedPlaceholderColor = LocalAppColors.current.hintTextColor,

            focusedBorderColor = LocalAppColors.current.actionTextColor,
            focusedTextColor = LocalAppColors.current.headerTextColor,
        ),
        visualTransformation = if (!isPasswordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = if (isIcon)
            KeyboardOptions(keyboardType = KeyboardType.Password)
        else
            KeyboardOptions.Default,
        trailingIcon = {
            if (isIcon) {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
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
