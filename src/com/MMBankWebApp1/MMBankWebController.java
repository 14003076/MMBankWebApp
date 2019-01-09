package com.MMBankWebApp1;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.moneymoney.account.SavingsAccount;
import com.moneymoney.account.service.SavingsAccountService;
import com.moneymoney.account.service.SavingsAccountServiceImpl;
import com.moneymoney.account.util.DBUtil;
import com.moneymoney.exception.AccountNotFoundException;
/**
 * 
 * Servlet implementation class MMBankController
 */

@WebServlet("*.mm")
public class MMBankWebController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestDispatcher dispatcher;
	protected boolean toggle;
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankapp_db", "root", "root");
			PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ACCOUNT");
			preparedStatement.execute();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletPath();
		SavingsAccountService savingsAccountService = new SavingsAccountServiceImpl();
		switch (path) {
		case "/addNewAccount.mm":
			response.sendRedirect("AddNewAccount.html");
			break;

		case "/newAccount.mm":
			String accountHolderName = request.getParameter("name");
			double accountBalance = Double.parseDouble(request.getParameter("amount"));
			boolean salary = request.getParameter("rd").equalsIgnoreCase("no") ? false : true;
			try {
				savingsAccountService.createNewAccount(accountHolderName,accountBalance, salary);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case "/close.mm":
			response.sendRedirect("CloseAccount.html");
			break;

		case "/closeForm.mm":
			int deleteAccount = Integer.parseInt(request.getParameter("accountNumber"));
			SavingsAccount savingsAccount;
			try {
				savingsAccountService.deleteAccount(deleteAccount);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/depositMoney.mm":
			response.sendRedirect("Deposit.html");
			break;

		case "/DepositForm.mm":
			int accountNumber = Integer.parseInt(request.getParameter("accountNumber"));
			double depositAmmount = Double.parseDouble(request.getParameter("amount"));
			try {
				savingsAccount = savingsAccountService.getAccountById(accountNumber);
				savingsAccountService.deposit(savingsAccount, depositAmmount);
				DBUtil.commit();
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
		      	}
			}
			break;
		case "/update.mm":
			response.sendRedirect("updateAccount.html");
			break;
		case "/updateForm.mm":
			int accountid = Integer.parseInt(request.getParameter("accountNumber"));
			SavingsAccount accountUpdate;
			try {
				accountUpdate = savingsAccountService.getAccountById(accountid);
				request.setAttribute("accounts", accountUpdate);
				dispatcher = request.getRequestDispatcher("updateDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException e2) {
				e2.printStackTrace();
			} catch (SQLException e2) {
				e2.printStackTrace();
			} catch (AccountNotFoundException e2) {
				e2.printStackTrace();
			}
			break;
		case "/updateAccount.mm":
			int accountId = Integer.parseInt(request.getParameter("accountNumber"));
			try {
				accountUpdate = savingsAccountService.getAccountById(accountId);
				String accHName = request.getParameter("name");
				accountUpdate.getBankAccount().setAccountHolderName(accHName);
				double accBal = Double.parseDouble(request.getParameter("balance"));
				boolean isSalary = request.getParameter("rd").equalsIgnoreCase("no") ? false : true;
				accountUpdate.setSalary(isSalary);
				savingsAccountService.updateAccount(accountUpdate);
				response.sendRedirect("getAll.mm");
			} catch (ClassNotFoundException | SQLException | AccountNotFoundException e2) {
				e2.printStackTrace();
			}
			break;

		case "/withDraw.mm":
			response.sendRedirect("WithDrawAmount.html");
			break;
		case "/WithdrawMoney.mm":
			int accNumber = Integer.parseInt(request
			.getParameter("accountNumber"));
			double withdrawAmmount = Double.parseDouble(request.getParameter("amount"));
			try {
				savingsAccount = savingsAccountService.getAccountById(accNumber);
				savingsAccountService.withdraw(savingsAccount, withdrawAmmount);
				DBUtil.commit();
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException| AccountNotFoundException e) {
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (Exception e) {
				try {
					DBUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			break;

		case "/Funds.mm":
			response.sendRedirect("FundTransfer.html");
			break;
		case "/Transaction.mm":
			int senderaccNumber = Integer.parseInt(request
			.getParameter("senderAccountNumber"));
			int receiveraccNumber = Integer.parseInt(request
			.getParameter("receiverAccountNumber"));
			double transferAmmount = Double.parseDouble(request.getParameter("amount"));
			try {
				SavingsAccount senderSavingsAccount = savingsAccountService.getAccountById(senderaccNumber);
				SavingsAccount receiverSavingsAccount = savingsAccountService.getAccountById(receiveraccNumber);
				savingsAccountService.fundTransfer(senderSavingsAccount,
				receiverSavingsAccount, transferAmmount);
				response.sendRedirect("index.html");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "/currentBalance.mm":
			response.sendRedirect("CheckCurrentBalance.html");
			break;
		case "/currentBalanceForm.mm":
			int checkBalance = Integer.parseInt(request.getParameter("accountNumber"));
			try {
				SavingsAccount checkAccBal = savingsAccountService.getCurrentBalance(checkBalance);
				PrintWriter out = response.getWriter();
				out.println("Yout Account Balance: " + checkAccBal);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (AccountNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "/getAll.mm":
			try {
				List<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				request.setAttribute("accounts", accounts);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case "/sortByName.mm":
			toggle = !toggle;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = arg0.getBankAccount().getAccountHolderName().compareTo(arg1.getBankAccount().getAccountHolderName());
						if (toggle == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case "/sortByNumber.mm":
			toggle = !toggle;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {

					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = arg0.getBankAccount().getAccountNumber()- (arg1.getBankAccount().getAccountNumber());
						if (toggle == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			break;

		case "/sortByBalance.mm":
			toggle = !toggle;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = (int) (arg0.getBankAccount().getAccountBalance() - arg1.getBankAccount().getAccountBalance());
						if (toggle == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			break;

		case "/sortBySalary.mm":
			toggle = !toggle;
			try {
				Collection<SavingsAccount> accounts = savingsAccountService.getAllSavingsAccount();
				List<SavingsAccount> accountSet = new ArrayList<>(accounts);
				Collections.sort(accountSet, new Comparator<SavingsAccount>() {
					@Override
					public int compare(SavingsAccount arg0, SavingsAccount arg1) {
						int result = Boolean.compare(arg0.isSalary(),arg1.isSalary());
						if (toggle == true) {
							return result;
						} else {
							return -result;
						}
					}
				});
				request.setAttribute("accounts", accountSet);
				dispatcher = request.getRequestDispatcher("AccountDetails.jsp");
				dispatcher.forward(request, response);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {

		// TODO Auto-generated method stub

	}

}