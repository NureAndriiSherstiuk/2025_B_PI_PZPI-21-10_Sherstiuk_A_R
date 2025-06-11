import { configureStore } from "@reduxjs/toolkit";
import raceReducer from "./race/raceSlice";
import userReducer from "./user/userSlice";

export const store = configureStore({
  reducer: {
    race: raceReducer,
    user: userReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
