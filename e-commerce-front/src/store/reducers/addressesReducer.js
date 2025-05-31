const initialState = {
    addresses: [],
    isLoading: false,
    btnLoader: false,
    error: null,
  };
  
  export default function addressReducer(state = initialState, action) {
    switch (action.type) {
      case "IS_FETCHING":
        return { ...state, isLoading: true };
      case "IS_SUCCESS":
        return { ...state, isLoading: false, btnLoader: false, error: null };
      case "IS_ERROR":
        return { ...state, isLoading: false, btnLoader: false, error: action.payload };
      case "BUTTON_LOADER":
        return { ...state, btnLoader: true };
      case "USER_ADDRESS":
        return { ...state, addresses: action.payload }; // Nadpisuje całą listę!
      default:
        return state;
    }
  }