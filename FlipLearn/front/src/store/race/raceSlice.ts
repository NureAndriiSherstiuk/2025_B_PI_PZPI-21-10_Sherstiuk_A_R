import { createSlice } from "@reduxjs/toolkit";

export interface Race {
  raceResult: any;
}

const initialState: Race = {
  raceResult: [],
};

export const raceSlice = createSlice({
  name: "race",
  initialState,
  reducers: {
    addParticipant: (state, action) => {
      state.raceResult = action.payload;
    },
    refresh: (state) => {
      state.raceResult = [];
    },
  },
});

export const { addParticipant, refresh } = raceSlice.actions;

export default raceSlice.reducer;
