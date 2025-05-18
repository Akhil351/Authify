import { createContext, useEffect, useState } from "react";
import { AppConstants } from "../util/constants.js";
import axios from "axios";
import { toast } from "react-toastify";

export const AppContext = createContext();

export const AppContextProvider = (props) => {
  axios.defaults.withCredentials = true;

  const backendURL = AppConstants.BACEND_URL;
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userData, setUserData] = useState(false);

  const getUserData = async () => {
    try {
      const response = await axios.get(`${backendURL}/profile`);
      const { status, data, error } = response.data;

      if (status === "success") {
        setUserData(data);
      } else {
        toast.error(error || "Unable to retrieve profile.");
      }
    } catch (error) {
      toast.error(
        error?.response?.data?.error ||
          error.message ||
          "Server error while fetching profile."
      );
    }
  };

  const getAuthState = async () => {
    try {
      const response = await axios.get(`${backendURL}/is-authenticated`);
      const { status, data, error } = response.data;

      if (status === "success" && data) {
        setIsLoggedIn(true);
        await getUserData();
      } else {
        setIsLoggedIn(false);
      }
    } catch (error) {
      console.error(error);
      //   toast.error(
      //     error?.response?.data?.error || "Failed to check authentication state."
      //   );
      setIsLoggedIn(false);
    }
  };

  useEffect(() => {
    getAuthState();
  }, []);

  const contextValue = {
    backendURL,
    isLoggedIn,
    setIsLoggedIn,
    userData,
    setUserData,
    getUserData,
  };

  return (
    <AppContext.Provider value={contextValue}>
      {props.children}
    </AppContext.Provider>
  );
};
