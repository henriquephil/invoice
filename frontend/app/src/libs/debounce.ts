import { useRef, useEffect, useMemo } from 'react';

export function useDebouncedCallback<A extends any[]>(
  callback: (...args: A) => void,
  wait: number
) {
  const callbackRef = useRef(callback);
  useEffect(() => {
    callbackRef.current = callback;
  });

  return useMemo(() => {
    let timeout: ReturnType<typeof setTimeout>;

    return (...args: A) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => {
        callbackRef.current(...args);
      }, wait);
    };
  }, [wait]);
}
