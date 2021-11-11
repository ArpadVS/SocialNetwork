import axiosAPI from '../axios/axios';
import { useState, useEffect } from 'react'
import authHeader from '../AuthHeader';
import { ToastrHelper } from '../../utils/toastrj';

const useAxiosFriendRequests = () => {
    const [friendRequests, setFriendRequests] = useState([]);
    const [error, setError] = useState(null);
    const [loadingRequests, setLoadingRequests] = useState(false);

    const fetchData = async () => {
        setLoadingRequests(true);
        try {
            const res = await axiosAPI.get("/requests", { headers: authHeader() });
            setFriendRequests(res.data);
            setError(null);
        } catch (err) {
            ToastrHelper.error(error.response.data)
            setError(err);
        } finally {
            setLoadingRequests(false);
        }
    };

    useEffect(() => {
        fetchData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return { friendRequests, error, loadingRequests };
};

export default useAxiosFriendRequests;