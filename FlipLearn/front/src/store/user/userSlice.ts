import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axiosInstance from "../../utils/axiosInstance";

export interface User {
  id: number;
  username: string;
  email: string;
  regDate: string;
  trustLevel: string | null;
  image: string;
  lang: string;
}

interface UserState {
  user: User;
}

const initialState: UserState = {
  user: {
    id: 0,
    username: "",
    email: "",
    regDate: "",
    trustLevel: null,
    image: "",
    lang: "",
  },
};

export const fetchUser = createAsyncThunk("user/fetchUser", async (token: string, { rejectWithValue }) => {
  try {
    const response = await axiosInstance.get("/User", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    return response.data;
  } catch (error: any) {
    return rejectWithValue(error.response?.data?.message || "Failed to fetch user");
  }
});

export const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    logout: (state) => {
      state.user = initialState.user;
    },
    updateUserProfile: (state, action) => {
      state.user = { ...state.user, ...action.payload };
    },
  },
  extraReducers: (builder) => {
    builder.addCase(fetchUser.fulfilled, (state, action) => {
      state.user = action.payload;
    });
  },
});

export const { logout, updateUserProfile } = userSlice.actions;

export default userSlice.reducer;
