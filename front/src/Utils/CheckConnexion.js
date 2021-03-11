import Axios from 'axios';
import { API_AUTH_URL, TOKEN } from '@/Constant.js'

export default async function CheckConnexion () {
    const token = localStorage.getItem(TOKEN);

    if (token === null)
        return false;

    const result = await Axios.get(API_AUTH_URL + '/me', {
        headers: {
            Authorization: `Bearer ${token}`
        }
    }).then(() => {return true})
    .catch(() => {return false});

    return result;
}