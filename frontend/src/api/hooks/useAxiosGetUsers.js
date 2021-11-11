import axiosAPI from '../axios/axios';
import { useState, useEffect } from 'react'
import authHeader from '../AuthHeader';
import { ToastrHelper } from '../../utils/toastrj';

const useAxiosGetUsers = () => {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState(null);
    const [loadingUsers, setLoadingUsers] = useState(false);

    const fetchData = async () => {
        setLoadingUsers(true);
        try {
            const res = await axiosAPI.get("/users", { headers: authHeader() });
            setUsers(res.data);
            setError(null);
        } catch (err) {
            ToastrHelper.error(error.response.data)
            setError(err);
        } finally {
            setLoadingUsers(false);
        }
    };

    useEffect(() => {
        fetchData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return { users, error, loadingUsers };
};

export default useAxiosGetUsers;