import React, {useEffect, useState} from "react";
import './styles.css';
import {Link, NavLink, Route, Router, Switch} from "react-router-dom";
import Book from "../Book";
import Api from "../../services/api";
import { useNavigate, useParams } from 'react-router-dom';

import Login from "../Login";
export default function NewBook({children}) {

    const [id, setId] = useState('');
    const [author, setAuthor] = useState('');
    const [launchDate, setLaunchDate] = useState('');
    const [price, setPrice] = useState('');
    const [title, setTitle] = useState('');

    const {bookId } = useParams();

    let navigate = useNavigate();
    const username = localStorage.getItem('username');
    const accessToken = localStorage.getItem('accessToken');

    async function loadBook() {
        try {
            const response = await Api.get(`api/book/v1/${bookId}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });
            let adjustedDate = response.data.launchDate.split('T')[0];

            setId(response.data.id);
            setAuthor(response.data.author);
            setLaunchDate(adjustedDate);
            setPrice(response.data.price);
            setTitle(response.data.title);
        } catch (e) {
            alert('Falha ao carregar livro, tente novamente.');
            navigate('/book')
        }
    }

    useEffect(() => {
        if (bookId === '0') return;
        else loadBook();
    }, [bookId]);

    async function saveOrUpdate(e) {
        e.preventDefault();

        const data = {
            title,
            author,
            launchDate,
            price,
        }

        try {
            if (bookId === '0') {
                await Api.post('api/book/v1', data, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });
            } else {
                data.id = id;
                await Api.put(`api/book/v1`, data, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });
            }

            navigate('/book');
        } catch (e) {
            alert('Falha ao criar novo livro, tente novamente.');
        }
    }

    return (
        <div className="page-container-new-book">
            <form onSubmit={saveOrUpdate}>
            <div className="row">
                <div className="col">
                    <input value={author} onChange={e => setAuthor(e.target.value)} type="text" className="form-control" placeholder="Author" aria-label="First name"/>
                </div>
                <div className="col">
                    <input value={title} onChange={e => setTitle(e.target.value)}  type="text" className="form-control" placeholder="Title" aria-label="Last name"/>
                </div>
            </div>
            <div className="row">
                <div className="col">
                    <input value={launchDate} onChange={e => setLaunchDate(e.target.value)}  type="date" className="form-control" placeholder="Launch Date" aria-label="First name"/>
                </div>
                <div className="col">
                    <input value={price} onChange={e => setPrice(e.target.value)}  type="text" className="form-control" placeholder="Price" aria-label="Last name"/>
                </div>
            </div>
            <button type="submit" className="btn btn-primary">Salvar</button>
            <NavLink className="btn btn-primary" to="/book">Voltar</NavLink>
            </form>
        </div>
    );

}