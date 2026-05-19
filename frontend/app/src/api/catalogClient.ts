import { z } from 'zod';
import { 
  CreateCustomerSchema, 
  UpdateCustomerSchema,
  CreateItemSchema,
  UpdateItemSchema,
  type CreateCustomerRequest, 
  type UpdateCustomerRequest, 
  type CustomerResponse, 
  type CreateItemRequest, 
  type UpdateItemRequest, 
  type ItemResponse, 
  ItemResponseSchema,
  CustomerResponseSchema
} from "#/api/catalogDtos";
import apiClient from "#/api/httpClient";

// --- Item Requests ---

export const createItem = async (itemData: CreateItemRequest): Promise<string> => {
  const validated = CreateItemSchema.parse(itemData);
  const response = await apiClient.post('/catalog/items', validated);
  return response.data;
};

export const listItems = async (filter?: string): Promise<ItemResponse[]> => {
  const response = await apiClient.get('/catalog/items', { params: { filter } });
  return z.array(ItemResponseSchema).parse(response.data);
};

export const getItem = async (itemId: string): Promise<ItemResponse> => {
  const response = await apiClient.get(`/catalog/items/${itemId}`);
  return ItemResponseSchema.parse(response.data);
};

export const updateItem = async (itemId: string, itemData: UpdateItemRequest): Promise<void> => {
  const validated = UpdateItemSchema.parse(itemData);
  await apiClient.patch(`/catalog/items/${itemId}`, validated);
};


// --- Customer Requests ---

export const createCustomer = async (customerData: CreateCustomerRequest): Promise<string> => {
  const validated = CreateCustomerSchema.parse(customerData);
  const response = await apiClient.post('/catalog/customers', validated);
  return response.data;
};

export const listCustomers = async (): Promise<CustomerResponse[]> => {
  const response = await apiClient.get('/catalog/customers');
  return z.array(CustomerResponseSchema).parse(response.data);
};

export const getCustomer = async (customerId: string): Promise<CustomerResponse> => {
  const response = await apiClient.get(`/catalog/customers/${customerId}`);
  return CustomerResponseSchema.parse(response.data);
};

export const updateCustomer = async (customerId: string, customerData: UpdateCustomerRequest): Promise<void> => {
  const validated = UpdateCustomerSchema.parse(customerData);
  await apiClient.patch(`/catalog/customers/${customerId}`, validated);
};