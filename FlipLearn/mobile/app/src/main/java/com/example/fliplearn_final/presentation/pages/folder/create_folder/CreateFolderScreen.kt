package com.example.fliplearn_final.presentation.pages.folder.create_folder

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fliplearn_final.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.fliplearn_final.presentation.ui.components.CustomText
import com.example.fliplearn_final.presentation.ui.components.CustomTextField
import com.example.fliplearn_final.presentation.ui.theme.LocalAppColors
import com.example.fliplearn_final.presentation.ui.theme.LocalAppTypography
import com.example.fliplearn_final.util.UiEvent

@Composable
fun CreateFolderScreen(
    viewModel: CreateFolderViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBar(onBack = onBack) }
    ) { padding ->
        CreateFolderScreenContent(
            modifier = Modifier.padding(padding),
            folderName = uiState.name,
            folderDescription = uiState.description,
            onNameChange = viewModel::onFolderNameChange,
            onDescriptionChange = viewModel::onFolderDescriptionChange,
            onCreateClick = viewModel::onCreateFolderClick
        )
    }
}

@Composable
fun CreateFolderScreenContent(
    modifier: Modifier,
    folderName: String,
    folderDescription: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        CustomTextField(
            value = folderName,
            onValueChange = onNameChange,
            placeholderText = "Назва папки"
        )

        CustomTextField(
            value = folderDescription,
            onValueChange = onDescriptionChange,
            placeholderText = "Опис папки"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 55.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onCreateClick,
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalAppColors.current.primaryButtonColor
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_create_folder),
                        contentDescription = "Create folder",
                        modifier = Modifier.size(20.dp)
                    )
                    CustomText(
                        text = "Створити папку",
                        style = LocalAppTypography.current.displayMedium,
                        color = LocalAppColors.current.headerTextColor,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(  onBack: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = "Створити папку",
                    style = LocalAppTypography.current.displayMedium,
                    color = LocalAppColors.current.headerTextColor
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
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
