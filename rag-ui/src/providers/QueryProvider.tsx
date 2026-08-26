import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';
import type { ReactNode } from 'react';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

interface Props {
  children: ReactNode;
}

const QueryProvider = ({
  children,
}: Props) => {

  return (
    <QueryClientProvider
      client={queryClient}
    >
      {children}
    </QueryClientProvider>
  );
};

export default QueryProvider;