import axiosAPI from '../axios/axios';
import { useState, useEffect } from 'react'
import authHeader from '../AuthHeader';
import { ToastrHelper } from '../../utils/toastrj';

const useAxiosGetFriends = () => {
    const [friends, setFriends] = useState([]);
    const [error, setError] = useState(null);
    const [friendsLoaded, setFriendsLoaded] = useState(false);

    const fetchData = async () => {
        setFriendsLoaded(true);
        try {
            const res = await axiosAPI.get("/users/friends", { headers: authHeader() });
            setFriends(res.data);
            setError(null);
        } catch (err) {
            ToastrHelper.error(error.response.data)
            setError(err);
        } finally {
            setFriendsLoaded(false);
        }
    };

    useEffect(() => {
        fetchData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return { friends, error, friendsLoaded };
};

export default useAxiosGetFriends;