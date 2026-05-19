import { z } from 'zod';
import {
  InvoiceResponseSchema,
  IssuedInvoiceResponseSchema,
  DraftInvoiceResponseItemSchema,
  InvoiceHeadResponseSchema,
  InvoiceSettingsResponseSchema,
  CreateInvoiceItemSchema,
  UpdateInvoiceItemSchema,
  UpdateInvoiceSchema,
  InvoiceSettingsSchema,
  type CreateInvoiceItemRequest,
  type DraftInvoiceResponseItem,
  type InvoiceResponse,
  type IssuedInvoiceResponse,
  type InvoiceHeadResponse,
  type InvoiceSettingsRequest,
  type InvoiceSettingsResponse,
  type UpdateInvoiceItemRequest,
  type UpdateInvoiceRequest,
} from "#/api/invoiceDtos";
import apiClient from "#/api/httpClient";

// --- Invoice Requests ---

export const createInvoice = async (): Promise<string> => {
  const response = await apiClient.post('/invoice/invoices');
  return response.data;
};

export const listInvoices = async (): Promise<InvoiceHeadResponse[]> => {
  const response = await apiClient.get('/invoice/invoices');
  return z.array(InvoiceHeadResponseSchema).parse(response.data);
};

export const findInvoice = async (invoiceId: string): Promise<InvoiceResponse> => {
  const response = await apiClient.get(`/invoice/invoices/${invoiceId}`);
  return InvoiceResponseSchema.parse(response.data);
};

export const updateInvoice = async (invoiceId: string, invoiceData: UpdateInvoiceRequest): Promise<void> => {
  const validated = UpdateInvoiceSchema.parse(invoiceData);
  await apiClient.patch(`/invoice/invoices/${invoiceId}`, validated);
};

export const deleteInvoice = async (invoiceId: string): Promise<void> => {
  await apiClient.delete(`/invoice/invoices/${invoiceId}`);
};

export const issueInvoice = async (invoiceId: string): Promise<IssuedInvoiceResponse> => {
  const response = await apiClient.post(`/invoice/invoices/${invoiceId}/issue`);
  return IssuedInvoiceResponseSchema.parse(response.data);
};

// --- Invoice Item Requests ---

export const createInvoiceItem = async (invoiceId: string, itemData: CreateInvoiceItemRequest): Promise<string> => {
  const validated = CreateInvoiceItemSchema.parse(itemData);
  const response = await apiClient.post(`/invoice/invoices/${invoiceId}/items`, validated);
  return response.data;
};

export const updateInvoiceItem = async (invoiceId: string, invoiceItemId: string, itemData: UpdateInvoiceItemRequest): Promise<DraftInvoiceResponseItem> => {
  const validated = UpdateInvoiceItemSchema.parse(itemData);
  const response = await apiClient.patch(`/invoice/invoices/${invoiceId}/items/${invoiceItemId}`, validated);
  return DraftInvoiceResponseItemSchema.parse(response.data);
};

export const deleteInvoiceItem = async (invoiceId: string, invoiceItemId: string): Promise<void> => {
  await apiClient.delete(`/invoice/invoices/${invoiceId}/items/${invoiceItemId}`);
};

// --- Settings Requests ---

export const getSettings = async (): Promise<InvoiceSettingsResponse> => {
  const response = await apiClient.get('/invoice/settings');
  return InvoiceSettingsResponseSchema.parse(response.data);
};

export const updateSettings = async (settingsData: InvoiceSettingsRequest): Promise<InvoiceSettingsResponse> => {
  const validated = InvoiceSettingsSchema.parse(settingsData);
  const response = await apiClient.patch('/invoice/settings', validated);
  return InvoiceSettingsResponseSchema.parse(response.data);
};
