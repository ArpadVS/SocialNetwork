import axiosAPI from '../axios/axios';
import { useState, useEffect } from 'react'

const useAxiosGetTimeline = (params) => {
  const [response, setResponse] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchData = async (params) => {
    setLoading(true);
    try {
      const res = await axiosAPI.request(params);

      console.log(res)
      setResponse(res.data);
      setError(null);
    } catch (err) {
      console.log(err)
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(params);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return { response, error, loading };
};

export default useAxiosGetTimeline;