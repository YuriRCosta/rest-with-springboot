import React, {useState, useEffect} from "react";
import {Link, useNavigate} from "react-router-dom";

import api from "../../services/api";

import './styles.css';
import Header from "../Header";

export default function Book({children}) {

    const [books, setBooks] = useState([]);
    const [page, setPage] = useState(0);

    let navigate = useNavigate();
    const username = localStorage.getItem('username');
    const accessToken = localStorage.getItem('accessToken');

    async function editBook(id) {
        try {
            navigate(`/book/new/${id}`);
        } catch (e) {
            alert('Falha ao atualizar livro, tente novamente.');
        }
    }

    async function deleteBook(id) {
        try {
            await api.delete(`api/book/v1/${id}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });
            setBooks(books.filter(book => book.id !== id));
        } catch (e) {
            alert('Falha ao deletar livro, tente novamente.');
        }
    }

    async function fetchMoreBooks() {
        const response = await api.get('api/book/v1', {
            headers: {
                Authorization: `Bearer ${accessToken}`
            },
            params: {
                page: page,
                size: "4",
                direction: 'asc',
            }
        });
        setBooks([...books, ...response.data._embedded.bookVOList]);
        setPage(page + 1);
    }

    useEffect(() => {
        fetchMoreBooks();
    }, []);

    return (
        <div className="page-container">
            <Header/>
            <div className="book-container">
                <Link className="btn btn-primary cad" to="/book/new/0">Cadastrar novo livro</Link>
            </div>

            <div className="books-area">
                <h1>Livros Registrados!</h1>
                <div className="container">
                    <div className="row">
                        <div className="col-12">
                            <ul>
                                {books.map(book => (
                                    <li key={book.id}>
                                        <strong>Livro:</strong>
                                        <p>{book.title}</p>
                                        <strong>Autor:</strong>
                                        <p>{book.author}</p>
                                        <strong>Preco:</strong>
                                        <p>{Intl.NumberFormat('pt-BR', {style: 'currency', currency: 'BRL'}).format(book.price)}</p>
                                        <strong>Data de lancamento:</strong>
                                        <p>{Intl.DateTimeFormat('pt-BR').format(new Date(book.launchDate))}</p>
                                        <button onClick={() => editBook(book.id)} type="button" className="btn btn-warning">Editar</button>
                                        <button onClick={() => deleteBook(book.id)} type="button" className="btn btn-danger">Excluir</button>
                                    </li>
                                ))}
                            </ul>

                            <button onClick={fetchMoreBooks} type="button" className="btn btn-primary">Carregar mais</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        );
}