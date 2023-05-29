import React from "react";
import {BrowserRouter, Route} from "react-router-dom";
import {useRoutes} from "react-router-dom";
import Login from "./pages/Login";
import Book from "./pages/Book";
import NewBook from "./pages/NewBook";

const Routess = () => {
    return useRoutes([
        {path: '/', element: <Login/>},
        {path: '/book', element: <Book/>},
        {path: '/book/new', element: <NewBook/>}
        ]
    );
}

export default Routess;