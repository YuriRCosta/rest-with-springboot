import React from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Login from "./pages/Login";
import Book from "./pages/Book";
import NewBook from "./pages/NewBook";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={ <Login/> } />
                <Route path="/book" element={ <Book/> } />
                <Route path="/book/new/:bookId" element={ <NewBook/> } />
            </Routes>
        </BrowserRouter>
    )
}

export default App
