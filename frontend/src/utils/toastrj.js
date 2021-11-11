import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export class ToastrHelper {
    static info = (message) => toast.info(message, {
        position: "bottom-right",
        autoClose: 4500,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: true,
        progress: undefined,
    });

    static success = (message) => toast.success(message, {
        position: "bottom-right",
        autoClose: 4500,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: true,
        progress: undefined,
    });

    static error = (message) => toast.error(message, {
        position: "bottom-right",
        autoClose: 4500,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: true,
        progress: undefined,
    });

    static warn = (message) => toast.warn(message, {
        position: "bottom-right",
        autoClose: 4500,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: true,
        progress: undefined,
    });

}