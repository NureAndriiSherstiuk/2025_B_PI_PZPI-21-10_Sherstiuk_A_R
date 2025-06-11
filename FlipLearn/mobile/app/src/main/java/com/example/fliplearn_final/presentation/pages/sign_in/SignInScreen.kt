package com.example.fliplearn_final.presentation.pages.sign_in

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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


@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToStart: () -> Unit,
    onNavigateToSignUp:() -> Unit,
    onNavigateToMain:() -> Unit
) {
    Scaffold(
        topBar = { TopBar(onNavigateToStart = onNavigateToStart) },
        content = {
            SignInScreenContent(
                modifier = Modifier.padding(it),
                viewModel = viewModel,
                onNavigateToSignUp = onNavigateToSignUp,
                onNavigateToMain = onNavigateToMain
            )
        }
    )
}


@Composable
private fun SignInScreenContent(
    modifier: Modifier,
    viewModel: SignInViewModel,
    onNavigateToSignUp: () -> Unit,
    onNavigateToMain:() -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        GoogleButton(
           onClick = {
               coroutineScope.launch {
                   viewModel.signInWithGoogle(
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
            text = "Електронна пошта або ім'я користувача",
            modifier = Modifier.padding(bottom = 8.dp),
            style = LocalAppTypography.current.labelLarge,
        )
        SignInOutlinedTextField(
            viewModel = viewModel,
            value = uiState.email,
            isIcon = false,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholderText = "Введіть адресу електронної пошти",
            isPasswordVisible = uiState.isPasswordVisible
        )
        uiState.fieldErrors["email"]?.let { error ->
            CustomText(
                text = error,
                color = LocalAppColors.current.notificationColor,
                style = LocalAppTypography.current.labelSmall
            )
        }

        CustomText(
            text = "Пароль",
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            style = LocalAppTypography.current.labelLarge
        )

        SignInOutlinedTextField(
            viewModel = viewModel,
            value = uiState.password,
            isIcon = true,
            onValueChange = { viewModel.onPasswordChange(it) },
            placeholderText = "Введіть пароль",
            isPasswordVisible = uiState.isPasswordVisible
        )
        uiState.fieldErrors["password"]?.let { error ->
            CustomText(
                text = error,
                color = LocalAppColors.current.notificationColor,
                style = LocalAppTypography.current.labelSmall
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CustomText(
                text = "Не пам'ятаєте ",
                color = LocalAppColors.current.primaryTextColor,
                style = LocalAppTypography.current.labelSmall
            )

            CustomText(
                text = "пароль",
                color = LocalAppColors.current.actionTextColor,
                style = LocalAppTypography.current.labelSmall
            )


            CustomText(
                text = " або ",
                color = LocalAppColors.current.primaryTextColor,
                style = LocalAppTypography.current.labelSmall
            )

            CustomText(
                text = "ім’я користувача",
                color = LocalAppColors.current.actionTextColor,
                style = LocalAppTypography.current.labelSmall
            )

            CustomText(
                text = "?",
                color = LocalAppColors.current.primaryTextColor,
                style = LocalAppTypography.current.labelSmall
            )
            Spacer(modifier = Modifier.height(10.dp))

        }
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
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = if (uiState.isLoading) "Завантаження..." else "Увійти",
                onClick = {
                    viewModel.signIn(
                        onSuccess = {
                            onNavigateToMain()
                        },
                        onError = {}
                    )
                }
            )

            CustomText(
                text = "Не маєте акаунту FlipLearn? ",
                modifier = Modifier.padding(top = 16.dp),
                style = LocalAppTypography.current.labelMedium
            )

            CustomText(
                text = "Створіть профіль.",
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable {
                        onNavigateToSignUp()
                    },
                style = LocalAppTypography.current.labelMedium,
                color = LocalAppColors.current.actionTextColor
            )
        }
        Spacer(modifier = Modifier.weight(.2f))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onNavigateToStart:() -> Unit
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
private fun GoogleButton(
    onClick: () -> Unit,
) {


    OutlinedButton(
        onClick = onClick,
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
            text = "Увійти через Google",
            modifier = Modifier.padding(start = 8.dp),
            style = LocalAppTypography.current.labelMedium,
            color = LocalAppColors.current.headerTextColor
        )
    }
}



@Composable
private fun SignInOutlinedTextField(
    viewModel: SignInViewModel,
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
                IconButton(onClick = { viewModel.onPasswordVisibilityToggle() }) {
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



