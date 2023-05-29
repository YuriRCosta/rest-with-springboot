import React from "react";
import './styles.css';
import {Link} from "react-router-dom";

export default function Header({children}) {

    async function logout() {
        localStorage.clear();
    }

    return (
        <div className="header-container">
            <nav className="navMenu">
                <a href="#">Home</a>
                <a href="#">Livro</a>
                <a href="#">Work</a>
                <a className="botao-sair" onClick={logout}>Sair</a>
                <div className="dot"></div>
            </nav>
        </div>
        );
}