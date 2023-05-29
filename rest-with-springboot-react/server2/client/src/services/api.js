import axios, {Axios} from "axios";

const api = axios.create({
    baseURL: 'http://localhost',
}
);

export default api;